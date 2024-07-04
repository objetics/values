/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic;

import java.util.ArrayList;
import java.util.List;
import volgyerdo.value.method.AssemblyIndex;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class ValueLogic {
    
    private static final List<Value> VALUES = new ArrayList<>();
    
    static{
        VALUES.add(new AssemblyIndex());
        
    }
    
}
