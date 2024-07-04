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
public class RLEInfo1 implements Value{
    
    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public String name() {
        return "RLE information with escape";
    }
    
    @Override
    public int version(){
        return 1;
    }
    
    @Override
    public  double value(String input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            int count = 1;
            char currentChar = input.charAt(i);

            // Kezeljük az escape karaktereket a bemenetben
            if (currentChar == '\\') {
                stringBuilder.append("\\\\");
                continue;
            }

            while (i + 1 < input.length() && currentChar == input.charAt(i + 1) && count < 255) {
                i++;
                count++;
            }

            // Ha a számláló értéke nagyobb, mint 2, használjuk az escape karaktert
            if (count > 2) {
                stringBuilder.append('\\');
                stringBuilder.append(currentChar);
                stringBuilder.append((char) count);
            } else {
                // Különben adjuk hozzá a karaktereket a számláló értéke nélkül
                for (int j = 0; j < count; j++) {
                    stringBuilder.append(currentChar);
                }
            }
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

