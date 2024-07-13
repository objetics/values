/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import volgyerdo.value.structure.Value;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class MaxInfo implements Value{
    
    @Override
    public String name() {
        return "Maximum information";
    }

    @Override
    public  double value(Collection values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        Set atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return Math.log(values.size()) / Math.log(2);
        }
        int N = values.size();
        return N * Math.log(K) / Math.log(2);
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        int K = countUniqueBytes(values);
        if (K == 1) {
            return Math.log(values.length) / Math.log(2);
        }
        int N = values.length;
        return N * Math.log(K) / Math.log(2);
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
