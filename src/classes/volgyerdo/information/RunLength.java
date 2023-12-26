/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class RunLength {

    public static double information(String input) {
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

        return ShannonInfo.information(stringBuilder.toString());
    }

    // Tesztelés
    public static void main(String[] args) {
        String input = "aaaasasdaa\\abbbbbaaaa";
        double encoded = information(input);
        System.out.println("Eredeti szöveg: " + input);
        System.out.println("Tömörített szöveg: " + (long)encoded);
    }
}

