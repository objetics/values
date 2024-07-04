/*
 * To change this license header, 
choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Collection;
import volgyerdo.value.structure.Value;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyIndex implements Value{

    @Override
    public String name() {
        return "Assembly index";
    }
    
    @Override
    public  double value(String s) {
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
        System.out.println(s + ": " + ((int)value(s)));
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
