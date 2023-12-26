/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class ShannonInfo1 {

    public static double information(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return Math.ceil(ShannonEntropy.entropy(array) * array.length);
    }
    
    public static double information(boolean[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }
    
    public static double information(byte[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(short[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(int[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(float[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(double[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(char[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(String[] values) {
        return Math.ceil(ShannonEntropy.entropy(values) * values.length);
    }

    public static double information(String values) {
        return information(CollectionUtils.convertStringToCharList(values));
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
        if(map.size() == 1){
            return Math.ceil(Math.log(values.size()) / Math.log(2));
        }
        double information = 0;
        for (Object x : values) {
            double frequency = ((double) map.get(x)) / values.size(); 
            information -= Math.log(frequency) / Math.log(2);
        }
        return Math.ceil(information);
    }

    private ShannonInfo1() {
    }

}
