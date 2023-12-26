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
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class ShannonInfo implements Info {

    public double info(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return ShannonEntropy.entropy(array) * array.length;
    }

    public double info(boolean[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(byte[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(short[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(int[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(float[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(double[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(char[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public double info(String[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    @Override
    public double info(List values) {
        return 0;
    }

    public double info(String values) {
        return info(values.toCharArray());
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
        double entropy = 0;
        for (Object x : values) {
            double frequency = ((double) map.get(x)) / values.size();
            entropy -= Math.log(frequency) / Math.log(2);
        }
        return entropy;
    }

}
