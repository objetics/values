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
 * Shannon Spectrum Minimum
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SSM1Info implements Info{
    
    private ShannonInfo shannon = new ShannonInfo();

    @Override
    public double info(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return info(array);
    }

    @Override
    public double info(boolean[] values) {
        return info(CollectionUtils.convertBooleanArrayToList(values));
    }

    @Override
    public double info(byte[] values) {
        return info(CollectionUtils.convertByteArrayToList(values));
    }

    @Override
    public double info(short[] values) {
        return info(CollectionUtils.convertShortArrayToList(values));
    }

    @Override
    public double info(int[] values) {
        return info(CollectionUtils.convertIntArrayToList(values));
    }

    @Override
    public double info(float[] values) {
        return info(CollectionUtils.convertFloatArrayToList(values));
    }

    @Override
    public double info(double[] values) {
        return info(CollectionUtils.convertDoubleArrayToList(values));
    }

    @Override
    public double info(char[] values) {
        return info(CollectionUtils.convertCharArrayToList(values));
    }

    @Override
    public double info(String[] values) {
        return info(Arrays.stream(values).collect(Collectors.toList()));
    }

    @Override
    public double info(String values) {
        return info(CollectionUtils.convertStringToCharList(values));
    }

    @Override
    public double info(List values) {
        if (values.size() <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return Math.log(values.size()) / Math.log(2);
        }
        int N = values.size();
        double atomicInfo = ShannonEntropy.entropy(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannon.info(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r + Math.log(values.size() / r) / Math.log(2);
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
