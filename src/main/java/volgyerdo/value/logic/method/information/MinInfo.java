/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method.information;

import java.util.Collection;
import volgyerdo.value.structure.Information;
import volgyerdo.value.structure.BaseValue;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@BaseValue(
    id = 11,
    category = "information",
    acronym = "IMIN",
    name = "Minimum Information",
    description = "Calculates the minimum possible information content for a given dataset. " +
                  "This metric represents the theoretical lower bound of information that " +
                  "must be contained in any representation of the data. Always returns 0 " +
                  "for homogeneous data and log2(n) for n-element datasets.",
    algorithm = "1. Check if input is null or empty; " +
             "2. Return 0 as minimum information represents the theoretical lower bound; " +
             "3. Minimum information is achieved when all elements are identical"
)
public class MinInfo implements Information {

    @Override
    public String name() {
        return "Minimum information";
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null) {
            return 0;
        }
        if (values.isEmpty()) {
            return 0;
        }
        if (values.size() == 1) {
            return 1;
        }
        return Math.log(values.size() + 1) / Math.log(2);
    }

    @Override
    public double value(byte[] values) {
        if (values == null) {
            return 0;
        }
        if(values.length == 0){
            return 0;
        }
        if(values.length == 1){
            return 1;
        }
        return Math.log(values.length + 1) / Math.log(2);
    }

}
