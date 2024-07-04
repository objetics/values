/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import volgyerdo.value.structure.Value;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 * Shannon Spectrum Minimum
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SSMInfo implements Value {

    private ShannonInfo shannonInfo = new ShannonInfo();
    private final ShannonEntropy shannonEntropy = new ShannonEntropy();

    @Override
    public String name() {
        return "SSM information";
    }

    @Override
    public double value(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return value(array);
    }

    @Override
    public double value(boolean[] values) {
        return value(CollectionUtils.convertBooleanArrayToList(values));
    }

    @Override
    public double value(byte[] values) {
        return value(CollectionUtils.convertByteArrayToList(values));
    }

    @Override
    public double value(short[] values) {
        return value(CollectionUtils.convertShortArrayToList(values));
    }

    @Override
    public double value(int[] values) {
        return value(CollectionUtils.convertIntArrayToList(values));
    }

    @Override
    public double value(float[] values) {
        return value(CollectionUtils.convertFloatArrayToList(values));
    }

    @Override
    public double value(double[] values) {
        return value(CollectionUtils.convertDoubleArrayToList(values));
    }

    @Override
    public double value(char[] values) {
        return value(CollectionUtils.convertCharArrayToList(values));
    }

    @Override
    public double value(String[] values) {
        return value(Arrays.stream(values).collect(Collectors.toList()));
    }

    @Override
    public double value(String values) {
        return value(CollectionUtils.convertStringToCharList(values));
    }

    @Override
    public double value(Collection values) {
        if (values.size() <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        int N = values.size();
        double atomicInfo = shannonEntropy.value(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannonInfo.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r;
            }
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private double maxInformation(int N, int K, int r) {
        int m = N / r;
        if (Math.pow(K, r) <= m) {
            return N * Math.log(K) / Math.log(2);
        } else {
            return m * Math.log(m) / Math.log(2);
        }
    }

}
