/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class ShannonInfo {

    public static double information(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return ShannonEntropy.entropy(array) * array.length;
    }
    
    public static double information(boolean[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }
    
    public static double information(byte[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(short[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(int[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(float[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(double[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(char[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(String[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(String values) {
        return information(values.toCharArray());
    }
    
    public static double information(Collection values) {
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

    private ShannonInfo() {
    }

}
