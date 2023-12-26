/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.List;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class CompressionInfo implements Info {

    public double info(Object object) {
        return ArrayUtils.toGZIPByteArray(object).length * 8;
    }

    public double info(byte[] values) {
        return ArrayUtils.toGZIP(values).length * 8;
    }

    public double info(short[] values) {
        return info(ArrayUtils.toByteArray(values));
    }

    public double info(int[] values) {
        return info(ArrayUtils.toByteArray(values));
    }

    public double info(float[] values) {
        return info(ArrayUtils.toByteArray(values));
    }

    public double info(double[] values) {
        return info(ArrayUtils.toByteArray(values));
    }

    public double info(char[] values) {
        return info(new String(values).getBytes());
    }

    @Override
    public double info(boolean[] values) {
        return 0;
    }

    @Override
    public double info(List values) {
        return 0;
    }

    public double info(String[] values) {
        double info = 0;
        for (String s : values) {
            info += info(s);
        }
        return info;
    }

    public double info(String values) {
        return info(values.getBytes());
    }

}
