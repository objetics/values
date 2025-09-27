
package volgyerdo.value.logic.method;
import volgyerdo.value.structure.BaseValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

import volgyerdo.value.structure.Value;

/**
 * MultiBlockRegressionValue (MBR)
 *
 * - Kis bemeneten (< minEstimationSize) a delegate pontos értékét adja.
 * - Nagy bemeneten (>= minEstimationSize) időkerethez (expectedTimeMillis) igazított
 *   mintavételezéssel több blokkméretre becsli F(B)-t, majd súlyozott
 *   lineáris regresszióval (y = a + k*(N/B - 1)) a teljes értéket: a ≈ C(X).
 *
 * Megjegyzések:
 * - Collection<?> esetén a bemenetet egyszer snapshot-oljuk egy ArrayList-be
 *   (indexelhetőség, O(N) referencia-másolás).
 * - minEstimationSize: byte[]-nél bájt, Collection-nél elemszám.
 */

public final class MultiBlockRegressionValue implements Value {

    private static final int DEFAULT_MIN_ESTIMATION_SIZE = 10000;
    private static final long DEFAULT_EXPECTED_TIME_MILLIS = 1000L;
    private final Value delegate;
    private final long expectedTimeMillis;
    private final int minEstimationSize;
    private final Random rnd = new Random(42);

    public MultiBlockRegressionValue(Value delegate) {
        this(delegate, DEFAULT_EXPECTED_TIME_MILLIS);
    }

    public MultiBlockRegressionValue(Value delegate, long expectedTimeMillis) {
        if (delegate == null) throw new IllegalArgumentException("delegate cannot be null");
        if (expectedTimeMillis < 0) throw new IllegalArgumentException("expectedTimeMillis must be >= 0");
        this.delegate = delegate;
        this.expectedTimeMillis = expectedTimeMillis;
        this.minEstimationSize = extractMinEstimationSize(delegate);
    }

    private int extractMinEstimationSize(Value delegate) {
        BaseValue annotation = delegate.getClass().getAnnotation(volgyerdo.value.structure.BaseValue.class);
        if (annotation != null) {
            return annotation.minEstimationSize();
        }
        return DEFAULT_MIN_ESTIMATION_SIZE;
    }

    @Override
    public int version() {
        return Math.max(delegate.version(), 1);
    }

    /* ============================= BYTE[] UTAK ============================= */

    @Override
    public double value(byte[] values) {
        if (values == null || values.length == 0) return 0.0;
        final int N = values.length;

        if (N < minEstimationSize) {
            return delegate.value(values);
        }

        TimingModel tm = calibrateTimingBytes(values, Math.min(4096, N), Math.min(65536, N));
        if (tm.estimateMillis(N) <= Math.max(1L, (long) (0.9 * expectedTimeMillis))) {
            return delegate.value(values);
        }

        List<Integer> blockSizes = chooseBlockSizes(N, /*minBlock*/256);
        if (blockSizes.size() < 2) return delegate.value(values);

        Map<Integer, Integer> samplesPerB = allocateSamples(blockSizes, tm, expectedTimeMillis);
        ensureAtLeastTwoPoints(blockSizes, samplesPerB);

        List<RegressionPoint> points = new ArrayList<>();
        for (int B : blockSizes) {
            int K = samplesPerB.getOrDefault(B, 0);
            if (K <= 0) continue;
            SampleStat stat = sampleBlocksBytes(values, B, K);
            int mBlocks = (int) Math.ceil(N / (double) B);
            double y = stat.mean * mBlocks;
            double se = (stat.std / Math.max(1.0, Math.sqrt(K))) * mBlocks;
            double x = (N / (double) B) - 1.0;
            double w = se > 0 ? 1.0 / (se * se) : 1.0;
            points.add(new RegressionPoint(x, y, w));
        }
        if (points.size() < 2) return delegate.value(values);

        RegressionResult rr = weightedLinearRegression(points);
        return Math.max(0.0, rr.intercept);
    }

    private TimingModel calibrateTimingBytes(byte[] data, int s1, int s2) {
        s1 = Math.max(1, Math.min(s1, data.length));
        s2 = Math.max(1, Math.min(s2, data.length));
        if (s1 == s2) s2 = Math.min(data.length, s1 * 2);
        long t1 = timeOneCallBytes(randomSliceBytes(data, s1));
        long t2 = timeOneCallBytes(randomSliceBytes(data, s2));
        return fitTimingModel(s1, t1, s2, t2);
    }

    private long timeOneCallBytes(byte[] slice) {
        long t0 = System.nanoTime();
        delegate.value(slice);
        long t1 = System.nanoTime();
        return Math.max(0L, (t1 - t0) / 1_000_000L);
    }

    private byte[] randomSliceBytes(byte[] data, int size) {
        size = Math.max(1, Math.min(size, data.length));
        int start = (data.length == size) ? 0 : rnd.nextInt(data.length - size);
        return Arrays.copyOfRange(data, start, start + size);
    }

    private SampleStat sampleBlocksBytes(byte[] data, int B, int K) {
        final int N = data.length;
        final int mBlocks = (int) Math.ceil(N / (double) B);
        K = Math.max(1, Math.min(K, mBlocks));
        int[] idxs = reservoirSample(mBlocks, K);
        double sum = 0.0, sumSq = 0.0;
        for (int idx : idxs) {
            int start = idx * B;
            int end = Math.min(N, start + B);
            byte[] slice = Arrays.copyOfRange(data, start, end);
            double v = delegate.value(slice);
            sum += v; sumSq += v * v;
        }
        return toStat(sum, sumSq, K);
    }

    /* ============================ COLLECTION UTAK ============================ */

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.isEmpty()) return 0.0;
        // Snapshot indexelhető listává
        final ArrayList<?> list = (values instanceof ArrayList<?> al) ? al : new ArrayList<>(values);
        final int N = list.size();

        if (N < minEstimationSize) {
            return delegate.value(list);
        }

        TimingModel tm = calibrateTimingColl(list, Math.min(64, N), Math.min(2048, N));
        if (tm.estimateMillis(N) <= Math.max(1L, (long) (0.9 * expectedTimeMillis))) {
            return delegate.value(list);
        }

        List<Integer> blockSizes = chooseBlockSizes(N, /*minBlock elements*/32);
        if (blockSizes.size() < 2) return delegate.value(list);

        Map<Integer, Integer> samplesPerB = allocateSamples(blockSizes, tm, expectedTimeMillis);
        ensureAtLeastTwoPoints(blockSizes, samplesPerB);

        List<RegressionPoint> points = new ArrayList<>();
        for (int B : blockSizes) {
            int K = samplesPerB.getOrDefault(B, 0);
            if (K <= 0) continue;
            SampleStat stat = sampleBlocksColl(list, B, K);
            int mBlocks = (int) Math.ceil(N / (double) B);
            double y = stat.mean * mBlocks;
            double se = (stat.std / Math.max(1.0, Math.sqrt(K))) * mBlocks;
            double x = (N / (double) B) - 1.0;
            double w = se > 0 ? 1.0 / (se * se) : 1.0;
            points.add(new RegressionPoint(x, y, w));
        }
        if (points.size() < 2) return delegate.value(list);

        RegressionResult rr = weightedLinearRegression(points);
        return Math.max(0.0, rr.intercept);
    }

    private TimingModel calibrateTimingColl(List<?> list, int s1, int s2) {
        s1 = Math.max(1, Math.min(s1, list.size()));
        s2 = Math.max(1, Math.min(s2, list.size()));
        if (s1 == s2) s2 = Math.min(list.size(), s1 * 2);
        long t1 = timeOneCallColl(list.subList(0, s1));
        long t2 = timeOneCallColl(list.subList(Math.max(0, list.size() - s2), list.size()));
        return fitTimingModel(s1, t1, s2, t2);
    }

    private long timeOneCallColl(List<?> slice) {
        long t0 = System.nanoTime();
        // A delegate value(Collection) metódusát hívjuk
        double dummy = delegate.value(slice);
        long t1 = System.nanoTime();
        return Math.max(0L, (t1 - t0) / 1_000_000L);
    }

    private SampleStat sampleBlocksColl(List<?> list, int B, int K) {
        final int N = list.size();
        final int mBlocks = (int) Math.ceil(N / (double) B);
        K = Math.max(1, Math.min(K, mBlocks));
        int[] idxs = reservoirSample(mBlocks, K);
        double sum = 0.0, sumSq = 0.0;
        for (int idx : idxs) {
            int start = idx * B;
            int end = Math.min(N, start + B);
            List<?> slice = list.subList(start, end); // O(1) view ArrayList esetén
            double v = delegate.value(slice);
            sum += v; sumSq += v * v;
        }
        return toStat(sum, sumSq, K);
    }

    /* ============================ KÖZÖS SEGÉDEK ============================ */

    private TimingModel fitTimingModel(int s1, long t1, int s2, long t2) {
        double b = (s2 != s1) ? ((double) (t2 - t1)) / (double) (s2 - s1) : (double) t1 / Math.max(1, s1);
        double a = t1 - b * s1;
        if (b < 0) b = 0;
        if (a < 0) a = Math.min(t1, t2) * 0.5;
        return new TimingModel(a, b);
    }

    private List<Integer> chooseBlockSizes(int N, int minBlock) {
        int[] targets = new int[]{64, 32, 16, 8, 4, 2};
        TreeSet<Integer> set = new TreeSet<>();
        for (int x : targets) {
            int B = N / x;
            if (B >= minBlock && B < N) set.add(B);
        }
        int B = Math.max(minBlock, N / 128);
        while (set.size() < 3 && B >= Math.max(8, minBlock / 2)) {
            if (B < N) set.add(B);
            B /= 2;
        }
        while (set.size() > 6) set.pollFirst();
        return new ArrayList<>(set);
    }

    private Map<Integer, Integer> allocateSamples(List<Integer> Bs, TimingModel tm, long budgetMillis) {
        Map<Integer, Integer> K = new HashMap<>();
        if (budgetMillis <= 0) {
            List<Integer> sorted = new ArrayList<>(Bs);
            sorted.sort(Comparator.naturalOrder());
            if (!sorted.isEmpty()) K.put(sorted.get(0), 1);
            if (sorted.size() > 1) K.put(sorted.get(1), 1);
            return K;
        }
        for (int B : Bs) K.put(B, 3);
        long est = estimateTotalMillis(K, tm);
        if (est <= budgetMillis) return K;

        double scale = budgetMillis / (double) Math.max(1, est);
        if (scale < 1.0) {
            for (int B : new ArrayList<>(K.keySet())) {
                int v = Math.max(1, (int) Math.floor(K.get(B) * scale));
                K.put(B, v);
            }
        }

        est = estimateTotalMillis(K, tm);
        if (est <= budgetMillis) return K;

        List<Integer> sortedDesc = new ArrayList<>(Bs);
        sortedDesc.sort(Comparator.reverseOrder());
        int idx = 0;
        while (est > budgetMillis && !sortedDesc.isEmpty()) {
            int B = sortedDesc.get(idx % sortedDesc.size());
            int v = K.getOrDefault(B, 0);
            if (v > 1) {
                K.put(B, v - 1);
                est = estimateTotalMillis(K, tm);
            } else {
                idx++;
            }
            if (idx > sortedDesc.size() * 4) break;
        }
        return K;
    }

    private void ensureAtLeastTwoPoints(List<Integer> blockSizes, Map<Integer, Integer> K) {
        long active = K.entrySet().stream().filter(e -> e.getValue() > 0).count();
        if (active < 2) {
            blockSizes.sort(Comparator.naturalOrder());
            K.clear();
            if (!blockSizes.isEmpty()) K.put(blockSizes.get(0), 1);
            if (blockSizes.size() > 1) K.put(blockSizes.get(1), 1);
        }
    }

    private long estimateTotalMillis(Map<Integer, Integer> K, TimingModel tm) {
        long sum = 0L;
        for (Map.Entry<Integer, Integer> e : K.entrySet()) {
            int B = e.getKey();
            int k = Math.max(0, e.getValue());
            long one = tm.estimateMillis(B);
            if (k > 0 && one > 0 && Long.MAX_VALUE / one < k) return Long.MAX_VALUE;
            sum += (long) k * one;
        }
        return sum;
    }

    private SampleStat toStat(double sum, double sumSq, int K) {
        double mean = sum / K;
        double var = 0.0;
        if (K > 1) var = Math.max(0.0, (sumSq - (sum * sum) / K) / (K - 1));
        double std = Math.sqrt(var);
        return new SampleStat(mean, std);
    }

    private int[] reservoirSample(int n, int k) {
        k = Math.max(1, Math.min(k, n));
        int[] res = new int[k];
        for (int i = 0; i < k; i++) res[i] = i;
        for (int i = k; i < n; i++) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);
            if (j < k) res[j] = i;
        }
        return res;
    }

    private RegressionResult weightedLinearRegression(List<RegressionPoint> pts) {
        double S = 0, Sx = 0, Sy = 0, Sxx = 0, Sxy = 0;
        for (RegressionPoint p : pts) {
            double w = p.w <= 0 ? 1.0 : p.w;
            S += w; Sx += w * p.x; Sy += w * p.y; Sxx += w * p.x * p.x; Sxy += w * p.x * p.y;
        }
        double denom = (S * Sxx - Sx * Sx);
        if (Math.abs(denom) < 1e-12) {
            double a = Sy / Math.max(1e-12, S);
            return new RegressionResult(a, 0.0);
        }
        double slope = (S * Sxy - Sx * Sy) / denom;
        double intercept = (Sy - slope * Sx) / S;
        return new RegressionResult(intercept, slope);
    }

    /* ============================ BELTÉRI OSZTÁLYOK ============================ */

    private static final class TimingModel {
        final double alphaMillis;         // hívás overhead
        final double betaMillisPerUnit;   // egységnyi növekmény (byte/elem) ideje

        TimingModel(double alphaMillis, double betaMillisPerUnit) {
            this.alphaMillis = Math.max(0.0, alphaMillis);
            this.betaMillisPerUnit = Math.max(0.0, betaMillisPerUnit);
        }

        long estimateMillis(int size) {
            double est = alphaMillis + betaMillisPerUnit * Math.max(0, size);
            if (est < 0) est = 0;
            return (long) Math.ceil(est);
        }
    }

    private static final class SampleStat {
        final double mean, std;
        SampleStat(double mean, double std) { this.mean = mean; this.std = std; }
    }

    private static final class RegressionPoint {
        final double x, y, w;
        RegressionPoint(double x, double y, double w) { this.x = x; this.y = y; this.w = w; }
    }

    private static final class RegressionResult {
        final double intercept, slope;
        RegressionResult(double intercept, double slope) { this.intercept = intercept; this.slope = slope; }
    }
}