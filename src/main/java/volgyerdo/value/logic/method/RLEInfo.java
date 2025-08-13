/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.value.structure.Value;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class RLEInfo implements Value {

    @Override
    public String name() {
        return "RLE information";
    }

    @Override
    public double value(byte[] input) {
        if (input == null || input.length <= 1) {
            return 0;
        }
        byte[] rle = new byte[input.length];
        int pos = 0;

        for (int i = 0; i < input.length; i++) {
            int count = 1;
            byte current = input[i];

            if (current == '\0') {
                rle[pos++] = current;
                rle[pos++] = current;
                continue;
            }

            while (i + 1 < input.length && current == input[i + 1] && count < 255) {
                 i++;
                count++;
            }

            if (count > 2) {
                rle[pos++] = '\0';
                rle[pos++] = (byte)count;
                rle[pos++] = current;
            } else {
                for (int j = 0; j < count; j++) {
                    rle[pos++] = current;
                }
            }
        }
        
        Set<?> set = new HashSet<>(CollectionUtils.convertByteArrayToList(rle));

        return pos * Math.log(set.size()) / Math.log(2);
    }
    

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        
        List<Object> rle = new ArrayList<>(values.size());
        
        Object[] input = values.toArray();

        for (int i = 0; i < input.length; i++) {
            int count = 1;
            Object current = input[i];

            if (current == null) {
                rle.add(current);
                rle.add(current);
                continue;
            }

            while (i + 1 < input.length && Objects.equals(current, input[i + 1]) && count < 255) {
                 i++;
                count++;
            }

            if (count > 2) {
                rle.add(null);
                rle.add(count);
                rle.add(current);
            } else {
                for (int j = 0; j < count; j++) {
                    rle.add(current);
                }
            }
        }
        
        Set<Object> set = new HashSet<>(rle);
       
        return rle.size() * Math.log(set.size()) / Math.log(2);
    }
}
