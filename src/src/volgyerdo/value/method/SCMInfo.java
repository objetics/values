/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;
import volgyerdo.value.structure.Value;

/**
 * Shannon Composition Minimum Information
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SCMInfo implements Value{
    
    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public String name() {
        return "SCM information";
    }
    
    public  double value(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return value(array);
    }

    public  double value(boolean[] values) {
        return value(CollectionUtils.convertBooleanArrayToList(values));
    }

    public  double value(byte[] values) {
        return value(CollectionUtils.convertByteArrayToList(values));
    }

    public  double value(short[] values) {
        return value(CollectionUtils.convertShortArrayToList(values));
    }

    public  double value(int[] values) {
        return value(CollectionUtils.convertIntArrayToList(values));
    }

    public  double value(float[] values) {
        return value(CollectionUtils.convertFloatArrayToList(values));
    }

    public  double value(double[] values) {
        return value(CollectionUtils.convertDoubleArrayToList(values));
    }

    public  double value(char[] values) {
        return value(CollectionUtils.convertCharArrayToList(values));
    }

    public  double value(String[] values) {
        return value(Arrays.stream(values).collect(Collectors.toList()));
    }

    public  double value(String values) {
        return value(CollectionUtils.convertStringToCharList(values));
    }

    public  double value(Collection values) {
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
                actualInfo += shannon.value(element);
            }
            actualInfo += shannon.value(parts);
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
