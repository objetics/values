/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method.entropy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.value.structure.Entropy;
import volgyerdo.value.structure.ValueType;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@ValueType(
    category = "entropy",
    name = "Shannon Entropy",
    description = "Calculates Shannon entropy of a dataset, measuring the average information content " +
                  "per symbol. Returns values between 0 (completely ordered data) and log2(n) " +
                  "(uniformly distributed data with n unique symbols). Based on the probability " +
                  "distribution of symbols in the data."
)
public class ShannonEntropy implements Entropy {

    @Override
    public String name() {
        return "Shannon entropy";
    }
    
    @Override
    public double value(boolean[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(short[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(int[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(float[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(double[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(char[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(String[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
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
    
    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
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

}
