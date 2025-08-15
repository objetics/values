/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method.information;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import volgyerdo.value.structure.Information;
import volgyerdo.value.structure.ValueType;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@ValueType(
    category = "information",
    acronym = "IRLES",
    name = "RLE + Shannon Information",
    description = "Combines Run-Length Encoding with Shannon information calculation. First applies " +
                  "RLE compression to detect repetitive patterns, then calculates Shannon information " +
                  "on the compressed representation. Provides enhanced information measurement " +
                  "for data with both repetitive sequences and statistical patterns.",
    pseudo = "1. Apply Run-Length Encoding to input data; " +
             "2. Convert RLE result into (value, count) pairs; " +
             "3. Create new dataset from RLE representation; " +
             "4. Calculate Shannon information on RLE-compressed data; " +
             "5. Return Shannon information of the compressed representation"
)
public class RLEShannonInfo implements Information {

    private final ShannonInfo shannon = new ShannonInfo();

    @Override
    public String name() {
        return "RLE + Shannon information";
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

        return shannon.value(rle);
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

        return shannon.value(rle);
    }
}
