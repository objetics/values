/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class ShannonInfo1 implements Info {

    public double info(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return Math.ceil(ShannonEntropy.entropy(array) * array.length);
    }

    public double info(boolean[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(byte[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(short[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(int[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(float[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(double[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(char[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(String[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public double info(String values) {
        return info(CollectionUtils.convertStringToCharList(values));
    }

    @Override
    public double info(List values) {
        return 0;
    }

    public double info(Collection values) {
        Map<Object, Integer> map = new HashMap<>();
        for (Object x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        if (map.size() == 1) {
            return Math.ceil(Math.log(values.size()) / Math.log(2));
        }
        double info = 0;
        for (Object x : values) {
            double frequency = ((double) map.get(x)) / values.size();
            info -= Math.log(frequency) / Math.log(2);
        }
        return Math.ceil(info);
    }

}
