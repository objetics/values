/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic.method.assembly;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.value.structure.Assembly;
import volgyerdo.value.structure.ValueType;

/**
 *
 * @author zsolt
 */
@ValueType(
    category = "assembly",
    acronym = "A",
    name = "Assembly Measure",
    description = "Calculates a weighted assembly measure that combines object assembly indices " +
                  "with their frequency in the dataset. Uses exponential weighting to emphasize " +
                  "the contribution of frequent, easily assemblable objects. Provides insight " +
                  "into the overall structural efficiency of a collection.",
    pseudo = "1. Count frequency of each unique object in collection; " +
             "2. Calculate assembly index for each unique object; " +
             "3. Apply exponential weighting: exp(assembly_index) * (frequency - 1); " +
             "4. Sum all weighted assembly values; " +
             "5. Normalize by total number of objects to get assembly measure"
)
public class AssemblyMeasure implements Assembly {

    private AssemblyIndexApprox ai = new AssemblyIndexApprox();

    @Override
    public String name() {
        return "Assembly";
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

        int NT = values.size();

        double sum = 0.0;
        double logNT = Math.log(NT);

        for (Map.Entry<Object, Integer> entry : counts.entrySet()) {
            Object o = entry.getKey();
            int ni = entry.getValue();
            double aiVal = ai.value(o);
            // e^(ai - log(NT)) == e^ai / NT
            sum += Math.exp(aiVal - logNT) * (ni - 1);
        }
        return sum;
    }

    @Override
    public double value(byte[] values) {
        return ai.value(values);
    }

}
