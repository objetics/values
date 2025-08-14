/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volgyerdo.value.structure;

/**
 * Marker interface for assembly-based value measurements.
 * Assembly measures analyze patterns and structures in data by examining
 * how elements can be assembled or reconstructed.
 *
 * @author Volgyerdo Nonprofit Kft.
 */
public interface Assembly extends Value {
    
    /**
     * Indicates whether this assembly measure uses approximation algorithms.
     * 
     * @return true if the measure uses approximation for performance reasons
     */
    default boolean isApproximate() {
        return false;
    }
    
    /**
     * Indicates whether this assembly measure uses indexing for optimization.
     * 
     * @return true if the measure uses indexing techniques
     */
    default boolean usesIndexing() {
        return false;
    }
}
