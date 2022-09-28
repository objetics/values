/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.text.DecimalFormat;
import java.util.Arrays;
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
public class SSMInfo {

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
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        int N = values.size();
        double atomicInfo = ShannonEntropy.entropy(values);
        absoluteMax = maxInformation(N, K, 1);
        
        DecimalFormat format = new DecimalFormat("0.0001");
        
        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = ShannonInfo.information(parts);
            System.out.println(format.format(actualInfo));
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

    private static double maxInformation(int N, int K, int r) {
        int m = N / r;
        if (Math.pow(K, r) <= m) {
            return N * Math.log(K) / Math.log(2);
        } else {
            return m * Math.log(m) / Math.log(2);
        }
    }

    private SSMInfo() {
    }

}
