package com.example.trongp;

import java.util.HashMap;
import java.util.Map;

public class OptimizationResult {
    public Map<String, Object> parameters;
    public double bestFitness;

    public OptimizationResult(Map<String, Object> parameters, double bestFitness) {
        this.parameters = new HashMap<>(parameters);
        this.bestFitness = bestFitness;
    }
}
