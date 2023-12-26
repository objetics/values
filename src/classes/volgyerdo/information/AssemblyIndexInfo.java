/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyIndexInfo {

    public static double info(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        Set<String> set = new HashSet<>();
        return infoRecursive(s, set) + 1;
    }

    public static double infoRecursive(String s, Set<String> set) {
        if (set.contains(s)) {
            return 0;
        }
        if (s.length() == 1) {
            return 1;
        }
        set.add(s);
        int half = s.length() / 2;
        String s1 = s.substring(0, half);
        String s2 = s.substring(half);
        return infoRecursive(s1, set) + infoRecursive(s2, set);
    }

    public static void main(String[] args) {
        print("1111111");
        print("4892347983467782365236785423956");
        print("3333333334352345436777777773456");
    }

    private static void print(String s) {
        System.out.println(s + ": " + ((int)info(s)));
    }
}
