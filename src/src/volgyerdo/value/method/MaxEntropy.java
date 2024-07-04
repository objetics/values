/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;
import volgyerdo.value.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MaxEntropy implements Value {

    @Override
    public String name() {
        return "Maximum entropy";
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
        Set atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return 0;
        }
        return Math.log(K) / Math.log(2);
    }

}
