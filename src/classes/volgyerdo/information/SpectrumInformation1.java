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
public class SpectrumInformation1 {

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
        if (atomicSet.size() > 1) {
            double atomicInfo = ShannonEntropy.entropy(values);
            for (int r = 1; r <= values.size() / 2; r++) {
                List<List> parts = CollectionUtils.breakApart(values, r);
                double info = 0;
                for (int i = 0; i < parts.size(); i++) {
                    info += Math.max(atomicInfo, ShannonEntropy.entropy(parts.get(i)) * parts.get(i).size());
                }
                info *= Math.pow(parts.size(), ShannonEntropy.entropy(parts) - 1.);
                if (minimumInfo == 0 || info < minimumInfo) {
                    minimumInfo = info;
                }
            }
        }
        return minimumInfo;
    }

    private SpectrumInformation1() {
    }

}
