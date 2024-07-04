/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.test;

import java.util.ArrayList;
import java.util.List;
import volgyerdo.value.method.AssemblyInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyCombinationsTest {

    
    public static void main(String[] args) {

        char[] atoms = {'a', 'b'};

        int n = 16;

        long[] info = new long[64];
        
        Value assemblyIndex = new AssemblyInfo();

        for (int i = 1; i <= n; i++) {
            System.out.println("Size: " + i);
            List<String> combinations = generateCombinations(atoms, i);
            for (String combination : combinations) {
                info[(int) assemblyIndex.value(combination)]++;
            }
        }

        System.out.println();

        for (int i = 0; i < info.length; i++) {
            System.out.println(i + ";" + info[i]);
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
