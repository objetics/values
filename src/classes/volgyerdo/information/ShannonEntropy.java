/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class ShannonEntropy {

    public static double entropy(boolean[] values) {
        Map<Boolean, Integer> map = new HashMap<>();
        for (boolean x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (boolean x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(byte[] values) {
        Map<Byte, Integer> map = new HashMap<>();
        for (byte x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (byte x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(short[] values) {
        Map<Short, Integer> map = new HashMap<>();
        for (short x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (short x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(int[] values) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (int x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(float[] values) {
        Map<Float, Integer> map = new HashMap<>();
        for (float x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (float x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(double[] values) {
        Map<Double, Integer> map = new HashMap<>();
        for (double x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (double x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(char[] values) {
        Map<Character, Integer> map = new HashMap<>();
        for (char x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (char x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(String[] values) {
        Map<String, Integer> map = new HashMap<>();
        for (String x : values) {
            Integer frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1);
            } else {
                map.put(x, frequency + 1);
            }
        }
        double entropy = 0;
        for (String x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.length; 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(Collection values) {
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
        for (Object x : map.keySet()) {
            double frequency = ((double) map.get(x)) / values.size(); 
            entropy -= frequency * (Math.log(frequency) / Math.log(2));
        }
        return entropy;
    }
    
    public static double entropy(String values) {
        return entropy(values.toCharArray());
    }

    private ShannonEntropy() {
    }

}
