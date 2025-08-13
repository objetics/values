/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.logic.method;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import volgyerdo.commons.object.ObjectUtils;
import volgyerdo.value.structure.Value;
import volgyerdo.commons.primitive.ArrayUtils;

/**
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class GZIPInfo implements Value {

    private final MinInfo minInfo = new MinInfo();
    private final MaxInfo maxInfo = new MaxInfo();

    @Override
    public String name() {
        return "GZIP information";
    }
    
    @Override
    public double value(String values) {
        if (values == null || values.length() <= 1) {
            return 0;
        }
        return value(values.getBytes());
    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        double gzipInfo = ArrayUtils.toGZIP(values).length * 8;
        double min = minInfo.value(values);
        double max = maxInfo.value(values);
        double minGzipInfo = ArrayUtils.toGZIP(new byte[values.length]).length * 8;
        double maxGzipInfo = ArrayUtils.toGZIP(generateRandomByteArray(values)).length * 8;

        return scaleToRange(gzipInfo, minGzipInfo, maxGzipInfo, min, max);
    }

    public static byte[] generateRandomByteArray(byte[] original) {
        if (original == null || original.length == 0) {
            return new byte[0];
        }

        Set<Byte> uniqueValues = new HashSet<>();
        for (byte b : original) {
            uniqueValues.add(b);
        }

        byte[] uniqueArray = new byte[uniqueValues.size()];
        int index = 0;
        for (Byte value : uniqueValues) {
            uniqueArray[index++] = value;
        }

        Random random = new Random();
        byte[] result = new byte[original.length];

        for (int i = 0; i < result.length; i++) {
            int randomIndex = random.nextInt(uniqueArray.length);
            result[i] = uniqueArray[randomIndex];
        }

        return result;
    }

    @Override
    public double value(Collection<?> input) {
        if (input == null || input.size() <= 1) {
            return 0;
        }
        byte[] values = ObjectUtils.serialize(input);
        double gzipInfo = ArrayUtils.toGZIP(values).length * 8;
        double min = minInfo.value(input);
        double max = maxInfo.value(input);
        double minGzipInfo = ArrayUtils.toGZIP(new byte[values.length]).length * 8;
        double maxGzipInfo = ArrayUtils.toGZIP(generateRandomByteArray(values)).length * 8;

        return scaleToRange(gzipInfo, minGzipInfo, maxGzipInfo, min, max);
    }

    public static double scaleToRange(double value, double originalMin, double originalMax, double newMin, double newMax) {
        if (originalMax == originalMin) {
            // Ha az eredeti tartomány szélessége nulla, minden érték az új tartomány közepére kerül
            return (newMin + newMax) / 2;
        }
        return newMin + ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin);
    }

}
