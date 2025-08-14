/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
import java.util.List;

import volgyerdo.value.logic.method.assembly.AssemblyMeasure;
import volgyerdo.value.logic.method.assembly.AssemblyIndex;
import volgyerdo.value.logic.method.assembly.AssemblyIndexApprox;
import volgyerdo.value.logic.method.information.GZIPInfo;
import volgyerdo.value.logic.method.assembly.GeneralAssembly;
import volgyerdo.value.logic.method.information.HuffmanInfo;
import volgyerdo.value.logic.method.information.MarkovInfo;
import volgyerdo.value.logic.method.entropy.MaxEntropy;
import volgyerdo.value.logic.method.information.MaxInfo;
import volgyerdo.value.logic.method.information.MinInfo;
import volgyerdo.value.logic.method.information.RLEInfo;
import volgyerdo.value.logic.method.information.RLEShannonInfo;
import volgyerdo.value.logic.method.information.SCMInfo;
import volgyerdo.value.logic.method.information.SSMInfo;
import volgyerdo.value.logic.method.entropy.ShannonEntropy;
import volgyerdo.value.logic.method.information.ShannonInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    private static final List<Value> VALUES = new ArrayList<>();

    static {
        VALUES.add(new AssemblyMeasure());
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
