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
import volgyerdo.commons.collection.CollectionUtils;
import volgyerdo.value.structure.Information;
import volgyerdo.value.logic.method.entropy.ShannonEntropy;

/**
 * Shannon Spectrum Minimum
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public class SSMInfo implements Information{
    
    private ShannonInfo shannonInfo = new ShannonInfo();
    private final ShannonEntropy shannonEntropy = new ShannonEntropy();

    @Override
    public String name() {
        return "SSM information";
    }
    

//    @Override
//    public double value(byte[] values) {
//        return value(CollectionUtils.convertByteArrayToList(values));
//    }

    @Override
    public double value(byte[] values) {
        if (values == null || values.length <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set<Byte> atomicSet = new HashSet<>();
        for(byte b : values){
            atomicSet.add(b);
        }
        int K = atomicSet.size();
        if (K == 1) {
            return Math.log(values.length) / Math.log(2);
        }
        int N = values.length;
        double atomicInfo = shannonEntropy.value(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<byte[]> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannonInfo.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r + Math.log(values.length / r) / Math.log(2);
            }
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }
    
    @Override
    public double value(Collection<?> values) {
        if (values.size() <= 1) {
            return 0;
        }
        double minimumInfo = 0;
        double absoluteMax = 0;
        Set<?> atomicSet = new HashSet<>(values);
        int K = atomicSet.size();
        if (K == 1) {
            return Math.log(values.size()) / Math.log(2);
        }
        int N = values.size();
        double atomicInfo = shannonEntropy.value(values);
        absoluteMax = maxInformation(N, K, 1);

        for (int r = 1; r <= N / 2; r++) {
            List<List> parts = CollectionUtils.breakApart(values, r, false);
            double maxInfo = maxInformation(N, K, r);
            double actualInfo = shannonInfo.value(parts);
            actualInfo = maxInfo == 0 ? 0 : actualInfo / maxInfo * absoluteMax;
            if (actualInfo == 0) {
                actualInfo = atomicInfo * r + Math.log(values.size() / r) / Math.log(2);
            }
            if (minimumInfo == 0 || actualInfo < minimumInfo) {
                minimumInfo = actualInfo;
            }
        }
        return minimumInfo;
    }

    private double maxInformation(int N, int K, int r) {
        int m = N / r;
        if (Math.pow(K, r) <= m) {
            return N * Math.log(K) / Math.log(2);
        } else {
            return m * Math.log(m) / Math.log(2);
        }
    }


}
