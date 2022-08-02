/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SpectrumInformation3 {

    public static double information(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return information(array);
    }

    public static double information(boolean[] values) {
        return information(CollectionUtils.convertBooleanArrayToList(values));
    }

    public static double information(byte[] values) {
        return information(CollectionUtils.convertByteArrayToList(values));
    }

    public static double information(short[] values) {
        return information(CollectionUtils.convertShortArrayToList(values));
    }

    public static double information(int[] values) {
        return information(CollectionUtils.convertIntArrayToList(values));
    }

    public static double information(float[] values) {
        return information(CollectionUtils.convertFloatArrayToList(values));
    }

    public static double information(double[] values) {
        return information(CollectionUtils.convertDoubleArrayToList(values));
    }

    public static double information(char[] values) {
        return information(CollectionUtils.convertCharArrayToList(values));
    }

    public static double information(String[] values) {
        return information(Arrays.stream(values).collect(Collectors.toList()));
    }

    public static double information(String values) {
        return information(CollectionUtils.convertStringToCharList(values));
    }

    public static double information(List values) {
        double minimumInfo = 0;
        Set atomicSet = new HashSet<>(values);
        int N = values.size();
        int n = atomicSet.size();
        if (n > 1) {
            for (int r = 1; r <= N / 2; r++) {
                List<List> parts = CollectionUtils.breakApart(values, r);
                Map<List, Double> p = new HashMap<>();
                for(List part : parts){
                    Double x = p.get(part);
                    if(x == null){
                        p.put(part, 1.0);
                    } else{
                        p.put(part, x + 1.0);
                    }
                }
                for(Entry<List, Double> e : p.entrySet()){
                    p.put(e.getKey(), e.getValue() / N);
                }
                double actualInfo = 0;
                for(List part : parts){
                    actualInfo -= Math.log(p.get(part)) / Math.log(2);
                }
                if(actualInfo == 0 && parts.size() > 0 && parts.get(0).size() > 0){
                    actualInfo = information(parts.get(0));
                }
                if (minimumInfo == 0 || actualInfo < minimumInfo) {
                    minimumInfo = actualInfo;
                }
            }
        }
        return minimumInfo;
    }

    private SpectrumInformation3() {
    }

}
