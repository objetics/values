package volgyerdo.test;

import java.util.ArrayList;
import java.util.List;

import volgyerdo.value.logic.method.assembly.InfoBasedAssembly;

public class AssemblyTest {
    public static void main(String[] args) {
        InfoBasedAssembly iba = new InfoBasedAssembly();
        String test = "CCOCCN";
        List<String> list = new ArrayList<>();
        list.add(test);
        double assembly = iba.value(list);
        System.out.println("Assembly: " + assembly);
    }
}
