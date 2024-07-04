/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.test;

import volgyerdo.value.method.AssemblyInfo;
import volgyerdo.value.method.RLEInfo;
import volgyerdo.value.method.SSM1Info;
import volgyerdo.value.method.ShannonInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class InformationTest1 {
    
    public static void main(String[] args) {
        
        String a = "TGTTCAGTAAAGCCTA";
        String b = "GTTCGGTAAAAGATAC";
        String c = "ACCTGTTCATACGCTA";
        
        Value assemblyInfo = new AssemblyInfo();
        Value rleInfo = new RLEInfo();
        Value shannonInfo = new ShannonInfo();
        Value ssmInfo = new SSM1Info();
        
        System.out.println("a:");
        
        System.out.println("Assembly: " + assemblyInfo.value(a));
        System.out.println("RLE: " + rleInfo.value(a));
        System.out.println("Shannon: " + shannonInfo.value(a));
        System.out.println("SSM: " + ssmInfo.value(a));
        
        System.out.println("b:");
        
        System.out.println("Assembly: " + assemblyInfo.value(b));
        System.out.println("RLE: " + rleInfo.value(b));
        System.out.println("Shannon: " + shannonInfo.value(b));
        System.out.println("SSM: " + ssmInfo.value(b));
        
        System.out.println("c:");
        
        System.out.println("Assembly: " + assemblyInfo.value(c));
        System.out.println("RLE: " + rleInfo.value(c));
        System.out.println("Shannon: " + shannonInfo.value(c));
        System.out.println("SSM: " + ssmInfo.value(c));
    }
}
