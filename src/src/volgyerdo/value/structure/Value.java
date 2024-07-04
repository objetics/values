/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.structure;

import java.util.Collection;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Value {
    
    String name();
    
    default int version(){
        return 0;
    }

    double value(Object object);

    double value(boolean[] values);

    double value(byte[] values);

    double value(short[] values);

    double value(int[] values);

    double value(float[] values);

    double value(double[] values);

    double value(char[] values);

    double value(String[] values);

    double value(String values);

    double value(Collection values);
}
