/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.Collection;
import volgyerdo.value.structure.Value;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class GZIPInfo implements Value {

    private final MaxInfo maxInfo = new MaxInfo();
    private final MinInfo minInfo = new MinInfo();
    
    @Override
    public String name() {
        return "GZIP information";
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        return ArrayUtils.toGZIP(values).length * 8;
    }

    @Override
    public double value(Collection values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        return ArrayUtils.toGZIPByteArray(values).length * 8;
    }

}
