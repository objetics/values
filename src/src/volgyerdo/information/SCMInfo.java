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
 * Shannon Composition Minimum Information
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SCMInfo {
    
    private final ShannonInfo shannon = new ShannonInfo();

    public  double info(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return info(array);
    }

    public  double info(boolean[] values) {
        return info(CollectionUtils.convertBooleanArrayToList(values));
    }

    public  double info(byte[] values) {
        return info(CollectionUtils.convertByteArrayToList(values));
    }

    public  double info(short[] values) {
        return info(CollectionUtils.convertShortArrayToList(values));
    }

    public  double info(int[] values) {
        return info(CollectionUtils.convertIntArrayToList(values));
    }

    public  double info(float[] values) {
        return info(CollectionUtils.convertFloatArrayToList(values));
    }

    public  double info(double[] values) {
        return info(CollectionUtils.convertDoubleArrayToList(values));
    }

    public  double info(char[] values) {
        return info(CollectionUtils.convertCharArrayToList(values));
    }

    public  double info(String[] values) {
        return info(Arrays.stream(values).collect(Collectors.toList()));
    }

    public  double info(String values) {
        return info(CollectionUtils.convertStringToCharList(values));
    }

    public  double info(List values) {
        if (values.size() <= 1) {
            return 0;
        }
        Set atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        double minimumInfo = Double.POSITIVE_INFINITY;
        int N = values.size();
        double absoluteMax = maxInformation(N, K, 1);
        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            Set<List> range = new HashSet(parts);
            double actualInfo = 0;
            for (List element : range) {
                actualInfo += shannon.info(element);
            }
            actualInfo += shannon.info(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private  double maxInformation(int N, int K, int r) {
        int m = N / r;
        return m * (r * Math.log(Math.min(r, K)) / Math.log(2) 
                + Math.log(Math.min(m, Math.pow(K, r)))/Math.log(2));
    }


}
