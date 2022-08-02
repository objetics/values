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
public class ShannonInformation {

    public static double information(Object object) {
        byte[] array = ArrayUtils.toByteArray(object);
        return ShannonEntropy.entropy(array) * array.length;
    }
    
    public static double information(boolean[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }
    
    public static double information(byte[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(short[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(int[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(float[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(double[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(char[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(String[] values) {
        return ShannonEntropy.entropy(values) * values.length;
    }

    public static double information(String values) {
        return information(values.toCharArray());
    }

    private ShannonInformation() {
    }

}
