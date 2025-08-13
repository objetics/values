/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package volgyerdo.value.logic.method;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import volgyerdo.value.structure.Value;

/**
 *
 * @author zsolt
 */
public class Assembly implements Value {

    private AssemblyIndexApprox ai = new AssemblyIndexApprox();

    @Override
    public String name() {
        return "Assembly";
    }

    @Override
    public double value(Collection values) {
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
