/*
 * To change this license header, 
choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import volgyerdo.value.structure.Value;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class AssemblyIndexApprox implements Value {

    @Override
    public String name() {
        return "Assembly index (approximation)";
    }

    @Override
    public double value(byte[] b) {
        if (b == null || b.length <= 1) {
            return 0;
        }
        Set<ByteArrayWrapper> set = new HashSet<>();
        return infoRecursive(b, set);
    }

    private double infoRecursive(byte[] b, Set<ByteArrayWrapper> set) {
        if (b.length <= 1) {
            return 0;
        }
        ByteArrayWrapper wrappedArray = new ByteArrayWrapper(b);
        if (set.contains(wrappedArray)) {
            return 0;
        }
        set.add(wrappedArray);
        int half = b.length / 2;
        byte[] b1 = Arrays.copyOfRange(b, 0, half);
        byte[] b2 = Arrays.copyOfRange(b, half, b.length);
        return infoRecursive(b1, set) + infoRecursive(b2, set) + 1;
    }

    private static class ByteArrayWrapper {

        private final byte[] array;

        public ByteArrayWrapper(byte[] array) {
            this.array = array;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ByteArrayWrapper that = (ByteArrayWrapper) obj;
            return Arrays.equals(array, that.array);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(array);
        }
    }

    @Override
    public double value(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return 0;
        }
        Set<Collection> set = new HashSet<>();
        return infoRecursive(collection, set);
    }

    public double infoRecursive(Collection s, Set<Collection> set) {
        if (s.size() <= 1) {
            return 0;
        }
        if (set.contains(s)) {
            return 0;
        }
        set.add(s);
        int half = s.size() / 2;
        Collection s1 = splitCollection(s, 0, half);
        Collection s2 = splitCollection(s, half, s.size());
        return infoRecursive(s1, set) + infoRecursive(s2, set) + 1;
    }

    private Collection splitCollection(Collection collection, int start, int end) {
        int currentIndex = 0;
        Collection<Object> subCollection = new ArrayList<>();
        for (Object obj : collection) {
            if (currentIndex >= start && currentIndex < end) {
                subCollection.add(obj);
            }
            currentIndex++;
        }
        return subCollection;
    }
}
