/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method;

import volgyerdo.value.structure.Value;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MaxInfo implements Value {

    @Override
    public String name() {
        return "Maximum information";
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
        Set<?> atomicSet = new HashSet<>(values);
        int k = atomicSet.size();
        int n = values.size();
        double v = n * Math.log(k) / Math.log(2);
        if (v > 500) {
            return v;
        }
        return value(n, k);
    }

    @Override
    public double value(byte[] values) {
        if (values == null) {
            return 0;
        }
        if (values.length == 0) {
            return 0;
        }
        if (values.length == 1) {
            return 1;
        }
        int k = countUniqueBytes(values);
        int n = values.length;
        double v = n * Math.log(k) / Math.log(2);
        if (v > 500) {
            return v;
        }
        return value(n, k);
    }

    private double value(double n, double k) {
        if (k == 1) {
            return Math.log(n + 1) / Math.log(2);
        }
        return Math.log((Math.pow(k, n + 1) - 1) / (k - 1)) / Math.log(2);
    }

    public static int countUniqueBytes(byte[] byteArray) {
        boolean[] seen = new boolean[256];
        int uniqueCount = 0;

        for (byte b : byteArray) {
            int index = b & 0xFF;
            if (!seen[index]) {
                seen[index] = true;
                uniqueCount++;
            }
        }

        return uniqueCount;
    }
}
