/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
import java.util.List;
import volgyerdo.value.logic.method.Assembly;
import volgyerdo.value.logic.method.AssemblyIndexApprox;
import volgyerdo.value.logic.method.AssemblyIndex;
import volgyerdo.value.logic.method.GZIPInfo;
import volgyerdo.value.logic.method.GeneralAssembly;
import volgyerdo.value.logic.method.HuffmanInfo;
import volgyerdo.value.logic.method.MarkovInfo;
import volgyerdo.value.logic.method.MaxEntropy;
import volgyerdo.value.logic.method.MaxInfo;
import volgyerdo.value.logic.method.MinInfo;
import volgyerdo.value.logic.method.RLEInfo;
import volgyerdo.value.logic.method.RLEShannonInfo;
import volgyerdo.value.logic.method.SCMInfo;
import volgyerdo.value.logic.method.SSMInfo;
import volgyerdo.value.logic.method.ShannonEntropy;
import volgyerdo.value.logic.method.ShannonInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    private static final List<Value> VALUES = new ArrayList<>();

    static {
        VALUES.add(new Assembly());
        VALUES.add(new GeneralAssembly());
        VALUES.add(new AssemblyIndexApprox());
        VALUES.add(new AssemblyIndex());
        VALUES.add(new GZIPInfo());
        VALUES.add(new HuffmanInfo());
        VALUES.add(new MaxEntropy());
        VALUES.add(new MaxInfo());
        VALUES.add(new RLEInfo());
        VALUES.add(new SCMInfo());
        VALUES.add(new SSMInfo());
        VALUES.add(new ShannonEntropy());
        VALUES.add(new ShannonInfo());
        VALUES.add(new MinInfo());
        VALUES.add(new RLEShannonInfo());
        VALUES.add(new MarkovInfo());
        VALUES.sort((o1, o2) -> o1.name().compareTo(o2.name()));
    }

    public static List<Value> values() {
        return new ArrayList<>(VALUES);
    }
}
