/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.test;

import java.text.DecimalFormat;
import volgyerdo.value.logic.method.ShannonInfo;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class BinaryTest1 {
    
    public static void main(String[] args){
        
        DecimalFormat format = new DecimalFormat("0.0000");
        
        Value info = new ShannonInfo();
        
        double logInfo = 0;
        double binaryInfo = 0;
        
        double x = 1023;
        String binary = "1111111111";  
        
        logInfo = Math.log(x) / Math.log(2);
        binaryInfo = info.value(binary);
        
        System.out.println("logInfo: " + (Math.log(x) / Math.log(2)));
        System.out.println("binaryInfo: " + info.value(binary));

        
    }
    
}
