/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.List;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class RunLengthInfo implements Info{
    
    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public  double info(String input) {
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

        return shannon.info(stringBuilder.toString());
    }

    // Tesztelés
    public  void main(String[] args) {
        String input = "aaaasasdaa\\abbbbbaaaa";
        double encoded = info(input);
        System.out.println("Eredeti szöveg: " + input);
        System.out.println("Tömörített szöveg: " + (long)encoded);
    }

    @Override
    public double info(Object object) {
        return 0;
    }

    @Override
    public double info(boolean[] values) {
        return 0;
    }

    @Override
    public double info(byte[] values) {
        return 0;
    }

    @Override
    public double info(short[] values) {
        return 0;
    }

    @Override
    public double info(int[] values) {
        return 0;
    }

    @Override
    public double info(float[] values) {
        return 0;
    }

    @Override
    public double info(double[] values) {
        return 0;
    }

    @Override
    public double info(char[] values) {
        return 0;
    }

    @Override
    public double info(String[] values) {
        return 0;
    }


    @Override
    public double info(List values) {
        return 0;
    }
}

