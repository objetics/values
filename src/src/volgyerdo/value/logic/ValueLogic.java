/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
import java.util.List;
import volgyerdo.value.method.AssemblyIndex;
import volgyerdo.value.method.AssemblyInfo;
import volgyerdo.value.method.GZIPInfo;
import volgyerdo.value.method.HuffmanInfo;
import volgyerdo.value.method.MaxEntropy;
import volgyerdo.value.method.MaxInfo;
import volgyerdo.value.method.MinInfo;
import volgyerdo.value.method.RLEInfo;
import volgyerdo.value.method.RLEInfo1;
import volgyerdo.value.method.SCMInfo;
import volgyerdo.value.method.SHInfo;
import volgyerdo.value.method.SSM1Info;
import volgyerdo.value.method.SSMInfo;
import volgyerdo.value.method.Shannon1Info;
import volgyerdo.value.method.ShannonEntropy;
import volgyerdo.value.method.ShannonInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class ValueLogic {

    private static final List<Value> VALUES = new ArrayList<>();

    static {
        VALUES.add(new AssemblyIndex());
        VALUES.add(new AssemblyInfo());
        VALUES.add(new GZIPInfo());
        VALUES.add(new HuffmanInfo());
        VALUES.add(new MaxEntropy());
        VALUES.add(new MaxInfo());
        VALUES.add(new RLEInfo());
        VALUES.add(new RLEInfo1());
        VALUES.add(new SCMInfo());
        VALUES.add(new SHInfo());
        VALUES.add(new SSM1Info());
        VALUES.add(new SSMInfo());
        VALUES.add(new Shannon1Info());
        VALUES.add(new ShannonEntropy());
        VALUES.add(new ShannonInfo());
        VALUES.add(new MinInfo());
        VALUES.sort((o1, o2) -> o1.name().compareTo(o2.name()));
    }

    public static List<Value> values() {
        return new ArrayList<>(VALUES);
    }
}
