/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Collection;
import volgyerdo.value.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class RLEInfo implements Value {

    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public String name() {
        return "RLE information";
    }

    @Override
    public double value(String input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            int count = 1;
            char currentChar = input.charAt(i);

            while (i + 1 < input.length() && currentChar == input.charAt(i + 1) && count < 255) {
                i++;
                count++;
            }

            stringBuilder.append(currentChar);
            stringBuilder.append(count);
        }

        return shannon.value(stringBuilder.toString());
    }

    @Override
    public double value(Object object) {
        return 0;
    }

    @Override
    public double value(boolean[] values) {
        return 0;
    }

    @Override
    public double value(byte[] values) {
        return 0;
    }

    @Override
    public double value(short[] values) {
        return 0;
    }

    @Override
    public double value(int[] values) {
        return 0;
    }

    @Override
    public double value(float[] values) {
        return 0;
    }

    @Override
    public double value(double[] values) {
        return 0;
    }

    @Override
    public double value(char[] values) {
        return 0;
    }

    @Override
    public double value(String[] values) {
        return 0;
    }

    @Override
    public double value(Collection values) {
        return 0;
    }
}
