/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic.method.assembly;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.value.structure.Value;
import volgyerdo.value.structure.Assembly;
import volgyerdo.value.structure.ValueType;
import volgyerdo.value.logic.method.information.GZIPInfo;

/**
 *
 * @author zsolt
 */
@ValueType(
    category = "assembly",
    acronym = "AG",
    name = "General Assembly",
    description = "Calculates a general assembly measure by analyzing the information content " +
                  "of individual objects in a collection and weighting them by their frequency. " +
                  "Uses GZIP compression as the default information measure to assess how much " +
                  "information each unique object contributes to the overall assembly structure.",
    pseudo = "1. Group objects in collection by their unique values; " +
             "2. For each unique object, calculate its information content using GZIP; " +
             "3. Weight each object's information by its frequency in the collection; " +
             "4. Sum all weighted information values; " +
             "5. Return total assembly information content"
)
public class GeneralAssembly implements Assembly {
    
    private final Value info;

    public GeneralAssembly() {
        this(new GZIPInfo());
    }
    
    public GeneralAssembly(Value info) {
        this.info = info;
    }
    
    @Override
    public String name() {
        return "General Assembly";
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

        int N = counts.size();

        double sum = 0.0;

        for (Map.Entry<Object, Integer> entry : counts.entrySet()) {
            Object o = entry.getKey();
            int ni = entry.getValue();
            double i = info.value(o);
            
            sum += i * Math.log(ni) / Math.log(2);
        }
        return sum / N;
    }

    @Override
    public double value(byte[] values) {
        return info.value(values);
    }
    
}
