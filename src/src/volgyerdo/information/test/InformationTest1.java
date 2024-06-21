/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.information.test;

import volgyerdo.information.AssemblyIndexInfo;
import volgyerdo.information.Info;
import volgyerdo.information.RunLengthInfo;
import volgyerdo.information.SSM1Info;
import volgyerdo.information.ShannonInfo;

/**
 *
 * @author zsolt
 */
public class InformationTest1 {
    
    public static void main(String[] args) {
        
        String a = "TGTTCAGTAAAGCCTA";
        String b = "GTTCGGTAAAAGATAC";
        String c = "ACCTGTTCATACGCTA";
        
        Info assemblyInfo = new AssemblyIndexInfo();
        Info rleInfo = new RunLengthInfo();
        Info shannonInfo = new ShannonInfo();
        Info ssmInfo = new SSM1Info();
        
        System.out.println("a:");
        
        System.out.println("Assembly: " + assemblyInfo.info(a));
        System.out.println("RLE: " + rleInfo.info(a));
        System.out.println("Shannon: " + shannonInfo.info(a));
        System.out.println("SSM: " + ssmInfo.info(a));
        
        System.out.println("b:");
        
        System.out.println("Assembly: " + assemblyInfo.info(b));
        System.out.println("RLE: " + rleInfo.info(b));
        System.out.println("Shannon: " + shannonInfo.info(b));
        System.out.println("SSM: " + ssmInfo.info(b));
        
        System.out.println("c:");
        
        System.out.println("Assembly: " + assemblyInfo.info(c));
        System.out.println("RLE: " + rleInfo.info(c));
        System.out.println("Shannon: " + shannonInfo.info(c));
        System.out.println("SSM: " + ssmInfo.info(c));
    }
}
