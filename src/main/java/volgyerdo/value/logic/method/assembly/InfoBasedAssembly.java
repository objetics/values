/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic.method.assembly;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import volgyerdo.commons.math.fast.FastMath;
import volgyerdo.value.logic.method.information.GZIPInfo;
import volgyerdo.value.structure.Assembly;
import volgyerdo.value.structure.BaseValue;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
@BaseValue(
    id = 4,
    category = "assembly",
    acronym = "IBA",
    name = "Information-based Assembly",
    description = "Calculates an information-based assembly measure by analyzing the information content " +
                  "of individual objects in a collection and weighting them by their frequency. " +
                  "Uses GZIP compression as the default information measure to assess how much " +
                  "information each unique object contributes to the overall assembly structure.",
    algorithm = "1. Group objects in collection by their unique values;\n" +
             "2. For each unique object, calculate its information content using GZIP;\n" +
             "3. Weight each object's information by its frequency in the collection;\n" +
             "4. Sum all weighted information values;\n" +
             "5. Return total assembly information content"
)
public class InfoBasedAssembly implements Assembly {
    
    private final Value info;

    public InfoBasedAssembly() {
        this(new GZIPInfo());
    }
    
    public InfoBasedAssembly(Value info) {
        this.info = info;
    }
    
    @Override
    public String name() {
        return "Information-based Assembly";
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }

        // Számoljuk meg az egyedi objektumokat és a példányszámukat
        Map<Object, Integer> counts = new HashMap<>();
        for (Object o : values) {
            counts.put(o, counts.getOrDefault(o, 0) + 1);
        }

        double N = 0;

        double sum = 0.0;

        for (Map.Entry<Object, Integer> entry : counts.entrySet()) {
            Object o = entry.getKey();
            int ni = entry.getValue();
            double l = FastMath.log2((double)ni);
            sum += info.value(o) * l; 
            N+=l;  
        }
        return sum / N;
    }

    @Override
    public double value(byte[] values) {
        return info.value(values);
    }
    
}
