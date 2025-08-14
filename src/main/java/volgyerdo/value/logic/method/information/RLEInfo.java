/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method.information;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import volgyerdo.value.logic.method.util.InfoNormalizer;
import volgyerdo.value.logic.method.util.ValueUtils;
import volgyerdo.value.structure.Information;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class RLEInfo implements Information {

    @Override
    public String name() {
        return "RLE information";
    }

    @Override
    public double value(byte[] input) {
        if (input == null || input.length <= 1) {
            return 0;
        }
        
        double rleInfo = calculateRleInfo(input);
        
        // Calculate min and max RLE info for normalization
        double minRleInfo = calculateRleInfo(new byte[input.length]); // All zeros
        double maxRleInfo = calculateRleInfo(ValueUtils.generateRandomByteArray(input)); // Random sequence
        
        // Normalize the RLE information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(rleInfo, minRleInfo, maxRleInfo, input);
    }

    private static double calculateRleInfo(byte[] input) {
        if (input == null || input.length <= 1) {
            return 0;
        }
        
        byte[] rle = ValueUtils.performRleEncoding(input);
        Set<Byte> set = new HashSet<>();
        for (byte b : rle) {
            set.add(b);
        }
        
        return rle.length * Math.log(set.size()) / Math.log(2);
    }

    @Override
    public double value(Collection<?> values) {
        if (values == null || values.size() <= 1) {
            return 0;
        }
        
        Object[] input = values.toArray();
        double rleInfo = calculateRleInfo(input);
        
        // Calculate min and max RLE info for normalization
        double minRleInfo = calculateRleInfo(new Object[values.size()]); // All nulls
        double maxRleInfo = calculateRleInfo(ValueUtils.generateRandomObjectArray(values)); // Random sequence
        
        // Normalize the RLE information between MinInfo and MaxInfo
        return InfoNormalizer.normalizeInfo(rleInfo, minRleInfo, maxRleInfo, values);
    }

    private static double calculateRleInfo(Object[] input) {
        if (input == null || input.length <= 1) {
            return 0;
        }
        
        List<Object> rle = ValueUtils.performRleEncoding(input);
        Set<Object> set = new HashSet<>(rle);
        
        return rle.size() * Math.log(set.size()) / Math.log(2);
    }
}
