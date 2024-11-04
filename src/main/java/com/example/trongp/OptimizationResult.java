// OptimizationResult.java
package com.example.trongp;

import java.util.Map;

public class OptimizationResult {
    public Map<String, Object> parameters;
    public double bestFitness;
    public double averageFitness;
    public double varianceFitness;

    public OptimizationResult(Map<String, Object> parameters, double bestFitness, double averageFitness, double varianceFitness) {
        this.parameters = parameters;
        this.bestFitness = bestFitness;
        this.averageFitness = averageFitness;
        this.varianceFitness = varianceFitness;
    }
}
