/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.information;

import java.util.List;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Info {

    double info(Object object);

    double info(boolean[] values);

    double info(byte[] values);

    double info(short[] values);

    double info(int[] values);

    double info(float[] values);

    double info(double[] values);

    double info(char[] values);

    double info(String[] values);

    double info(String values);

    double info(List values);
}
