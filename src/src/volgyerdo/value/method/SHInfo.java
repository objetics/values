/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import volgyerdo.value.structure.Value;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 * Shannon Hierarchy Information
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SHInfo implements Value{
    
    private ShannonInfo shannon = new ShannonInfo();

        @Override
    public String name() {
        return "SH information";
    }
    
    @Override
    public  double value(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return value(array);
    }

    @Override
    public  double value(boolean[] values) {
        return value(CollectionUtils.convertBooleanArrayToList(values));
    }

    @Override
    public  double value(byte[] values) {
        return value(CollectionUtils.convertByteArrayToList(values));
    }

    @Override
    public  double value(short[] values) {
        return value(CollectionUtils.convertShortArrayToList(values));
    }

    @Override
    public  double value(int[] values) {
        return value(CollectionUtils.convertIntArrayToList(values));
    }

    @Override
    public  double value(float[] values) {
        return value(CollectionUtils.convertFloatArrayToList(values));
    }

    @Override
    public  double value(double[] values) {
        return value(CollectionUtils.convertDoubleArrayToList(values));
    }

    @Override
    public  double value(char[] values) {
        return value(CollectionUtils.convertCharArrayToList(values));
    }

    @Override
    public  double value(String[] values) {
        return value(Arrays.stream(values).collect(Collectors.toList()));
    }

    @Override
    public  double value(String values) {
        return value(CollectionUtils.convertStringToCharList(values));
    }

    @Override
    public  double value(Collection values) {
        if (values.size() <= 1) {
            return 0;
        }
        int N = values.size();
        int r = N / 2;
        List<List> parts = CollectionUtils.breakApart(values, r, false);
        double info = shannon.value(parts) + value(parts.get(0)) + value(parts.get(1));
//        info *= values.size() / (double) (parts.size() * r);
        return info;
    }

}
