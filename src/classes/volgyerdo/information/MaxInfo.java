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
public class MaxInfo implements Info{
    
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
        int N = values.size();
        return N * Math.log(K) / Math.log(2);
    }

  
    
}
