/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyIndexInfo implements Info{

    @Override
    public  double info(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        Set<String> set = new HashSet<>();
        return infoRecursive(s, set);
    }

    public  double infoRecursive(String s, Set<String> set) {
        if(s.length() <= 1){
            return 0;
        }
        if(set.contains(s)){
            return 0;
        }
        set.add(s);
        int half = s.length() / 2;
        String s1 = s.substring(0, half);
        String s2 = s.substring(half);
        return infoRecursive(s1, set) + infoRecursive(s2, set) + 1;
    }

    public  void main(String[] args) {
        
        print("11111");
        print("111111");
        print("1111111");
    }

    private  void print(String s) {
        System.out.println(s + ": " + ((int)info(s)));
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
