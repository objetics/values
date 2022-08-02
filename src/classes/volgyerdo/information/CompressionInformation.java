/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class CompressionInformation {

    public static double information(Object object) {
        return ArrayUtils.toGZIPByteArray(object).length * 8;
    }
    
    public static double information(byte[] values) {
        return ArrayUtils.toGZIP(values).length * 8;
    }
    
    public static double information(short[] values) {
        return information(ArrayUtils.toByteArray(values));
    }

    public static double information(int[] values) {
        return information(ArrayUtils.toByteArray(values));
    }

    public static double information(float[] values) {
        return information(ArrayUtils.toByteArray(values));
    }

    public static double information(double[] values) {
        return information(ArrayUtils.toByteArray(values));
    }

    public static double information(char[] values) {
        return information(new String(values).getBytes());
    }

    public static double information(String[] values) {
        double information = 0;
        for(String s : values){
            information += information(s);
        }
        return information;
    }

    public static double information(String values) {
        return information(values.getBytes());
    }

    private CompressionInformation() {
    }

}
