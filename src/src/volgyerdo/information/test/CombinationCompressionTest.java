/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import volgyerdo.information.AssemblyIndexInfo;
import volgyerdo.information.HuffmanInfo;
import volgyerdo.information.Info;
import volgyerdo.information.RunLengthInfo;
import volgyerdo.information.SSM1Info;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class CombinationCompressionTest {
    
    private static DecimalFormat format = new DecimalFormat("0.0000");

    public static void main(String[] args) {

        char[] atoms = {'a', 'b'};

        int n = 16;

        Info SSM1 = new SSM1Info();
        Info assemblyIndex = new AssemblyIndexInfo();
        Info huffman = new HuffmanInfo();
        Info runLength = new RunLengthInfo();

        for (int i = 1; i <= n; i++) {
            List<String> combinations = generateCombinations(atoms, i);
            double[] data = new double[4];
            int k = 0;
            for (String combination : combinations) {
                data[0] += assemblyIndex.info(combination);
                data[1] += huffman.info(combination);
                data[2] += SSM1.info(combination);
                data[3] += runLength.info(combination);
                k++;
            }
            for (int j = 0; j < data.length; j++) {
                data[j] /= k;
            }

            System.out.println(
                    i + ";" + format.format(data[0]) + ";" + format.format(data[1]) + ";" + 
                            format.format(data[2]) + ";" + format.format(data[3]));
        }

    }

    public static List<String> generateCombinations(char[] atoms, int n) {
        List<String> result = new ArrayList<>();
        generateCombinationsRecursive(atoms, "", n, result);
        return result;
    }

    private static void generateCombinationsRecursive(char[] atoms, String current, int n, List<String> result) {
        if (current.length() == n) {
            result.add(current);
            return;
        }

        for (char c : atoms) {
            generateCombinationsRecursive(atoms, current + c, n, result);
        }
    }

}
