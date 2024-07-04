/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import volgyerdo.value.structure.Value;
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
public class Shannon1Info implements Value {

    private final ShannonEntropy shannonEntropy = new ShannonEntropy();
    
    @Override
    public String name() {
        return "Shannon information";
    }
    
    @Override
    public int version(){
        return 1;
    }
    
    @Override
    public double value(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return Math.ceil(shannonEntropy.value(array) * array.length);
    }

    @Override
    public double value(boolean[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(byte[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(short[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(int[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(float[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(double[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(char[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(String[] values) {
        return Math.ceil(shannonEntropy.value(values) * values.length);
    }

    @Override
    public double value(String values) {
        return value(CollectionUtils.convertStringToCharList(values));
    }

    @Override
    public double value(Collection values) {
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
