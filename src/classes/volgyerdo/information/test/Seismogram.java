/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class Seismogram {

    public static void main(String[] args) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(
                "/media/Kutatás/Információmérés/Publikációk/Mintázatok/ECG signal.dat").toPath());
        for (int i = 0; i < bytes.length; i++) {
            System.out.println(bytes[i]);
//                System.out.print(String.format("%8s", Integer.toBinaryString(bytes[i] & 0xFF)).replace(' ', '0')+" ");
        }
 

    }



}
