/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import volgyerdo.value.structure.Value;
import java.util.Collection;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MinInfo implements Value {

    @Override
    public String name() {
        return "Minimum information";
    }

    @Override
    public double value(Collection values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        return Math.log(values.size()) / Math.log(2);
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return Math.log(values.length) / Math.log(2);
    }

}
