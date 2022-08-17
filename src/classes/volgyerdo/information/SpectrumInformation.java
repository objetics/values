/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SpectrumInformation {

    public static double information(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return information(array);
    }

    public static double information(boolean[] values) {
        return information(CollectionUtils.convertBooleanArrayToList(values));
    }

    public static double information(byte[] values) {
        return information(CollectionUtils.convertByteArrayToList(values));
    }

    public static double information(short[] values) {
        return information(CollectionUtils.convertShortArrayToList(values));
    }

    public static double information(int[] values) {
        return information(CollectionUtils.convertIntArrayToList(values));
    }

    public static double information(float[] values) {
        return information(CollectionUtils.convertFloatArrayToList(values));
    }

    public static double information(double[] values) {
        return information(CollectionUtils.convertDoubleArrayToList(values));
    }

    public static double information(char[] values) {
        return information(CollectionUtils.convertCharArrayToList(values));
    }

    public static double information(String[] values) {
        return information(Arrays.stream(values).collect(Collectors.toList()));
    }

    public static double information(String values) {
        return information(CollectionUtils.convertStringToCharList(values));
    }

    public static double information(List values) {
        if (values.size() <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set atomicSet = new HashSet<>(values);
        int N = values.size();
        int n = atomicSet.size();
        double atomicInfo = ShannonEntropy.entropy(values);
        absoluteMax = maxInformation(N, n, 1);
        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, n, r);
            double actualInfo = ShannonEntropy.entropy(parts) * parts.size();
            actualInfo = actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r;
            }
            actualInfo *= values.size() / (double) (parts.size() * r);
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private static double maxInformation(int N, int n, int r) {
        if (Math.pow(n, r) <= N / r) {
            return N * Math.log(n) / Math.log(2);
        } else {
            return (N / r) * Math.log(N / r) / Math.log(2);
        }
    }

    private SpectrumInformation() {
    }

}
