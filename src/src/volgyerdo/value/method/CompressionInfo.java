/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Collection;
import volgyerdo.value.structure.Value;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class CompressionInfo implements Value {

    @Override
    public String name() {
        return "GZIP information";
    }
    
    @Override
    public double value(Object object) {
        return ArrayUtils.toGZIPByteArray(object).length * 8;
    }

    @Override
    public double value(byte[] values) {
        return ArrayUtils.toGZIP(values).length * 8;
    }

    @Override
    public double value(short[] values) {
        return value(ArrayUtils.toByteArray(values));
    }

    @Override
    public double value(int[] values) {
        return value(ArrayUtils.toByteArray(values));
    }

    @Override
    public double value(float[] values) {
        return value(ArrayUtils.toByteArray(values));
    }

    @Override
    public double value(double[] values) {
        return value(ArrayUtils.toByteArray(values));
    }

    @Override
    public double value(char[] values) {
        return value(new String(values).getBytes());
    }

    @Override
    public double value(boolean[] values) {
        return 0;
    }

    @Override
    public double value(Collection values) {
        return 0;
    }

    @Override
    public double value(String[] values) {
        double info = 0;
        for (String s : values) {
            info += value(s);
        }
        return info;
    }

    @Override
    public double value(String values) {
        return value(values.getBytes());
    }

}
