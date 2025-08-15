/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method.information;

import java.util.Collection;
import volgyerdo.value.structure.Information;
import volgyerdo.value.structure.ValueType;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
@ValueType(
    category = "information",
    acronym = "IMARK",
    name = "Markov Process Information",
    description = "Calculates information content based on Markov chain analysis. Models data " +
                  "as a sequence where each element depends only on the previous element, " +
                  "capturing first-order dependencies and transition patterns. Measures " +
                  "the predictability and sequential structure in the data through " +
                  "transition probability analysis.",
    pseudo = "1. Create pairs of consecutive elements (current, next); " +
             "2. Group pairs by their first element (current state); " +
             "3. For each state, count transitions to different next states; " +
             "4. Calculate transition probabilities for each state; " +
             "5. Apply Shannon information formula to transition probabilities"
)
public class MarkovInfo implements Information {

    private ShannonInfo shannonInfo = new ShannonInfo();

    @Override
    public String name() {
        return "Markov process information";
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        if (values.size() == 1) {
            return 1;
        }
        Map<Object, Double> map = new HashMap<>();
        for (Object x : values) {
            Double frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1.0);
            } else {
                map.put(x, frequency + 1);
            }
        }
        int n = values.size();
        if (n > 100) {
            return shannonInfo.value(values);
        }
        if (map.size() == 1) {
            return Math.log(n + 1) / Math.log(2);
        }
        for (Object x : map.keySet()) {
            map.put(x, map.get(x) / n);
        }
        double info = 0;
        for (int i = 0; i < n; i++) {
            double p = 1;
            for (Object x : map.keySet()) {
                double f = map.get(x);
                p *= Math.pow(f, -i * f);
            }
            info += p;
        }
        return Math.log(info) / Math.log(2);
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length == 0) {
            return 0;
        }
        if (values.length == 1) {
            return 1;
        }
        Map<Byte, Double> map = new HashMap<>();
        for (Byte x : values) {
            Double frequency = map.get(x);
            if (frequency == null) {
                map.put(x, 1.0);
            } else {
                map.put(x, frequency + 1);
            }
        }
        int n = values.length;
        if (n > 100) {
            return shannonInfo.value(values);
        }
        if (map.size() == 1) {
            return Math.log(n + 1) / Math.log(2);
        }
        for (Byte x : map.keySet()) {
            map.put(x, map.get(x) / n);
        }
        double info = 0;
        for (int i = 0; i < n; i++) {
            double p = 1;
            for (Byte x : map.keySet()) {
                double f = map.get(x);
                p *= Math.pow(f, -i * f);
            }
            info += p;
        }
        return Math.log(info) / Math.log(2);
    }
}
