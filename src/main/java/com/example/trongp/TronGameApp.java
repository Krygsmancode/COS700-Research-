package com.example.trongp;

import java.util.Random;

import com.example.trongp.GP.GPParameters;

// In TronGameApp.java

public class TronGameApp {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true"); // Run in headless mode

        int[] seeds = {453};

        int numberOfTrials = 11; // Number of trials per seed

        for (int seedIndex = 0; seedIndex < seeds.length; seedIndex++) {
            int seed = seeds[seedIndex];
            Random random = new Random(seed); // Initialize Random with the current seed

            // Update GPParameters.SEED to the current seed
            GPParameters.SEED = seed;

            // Initialize ParameterOptimizer correctly
            ParameterOptimizer optimizer = new ParameterOptimizer(random, seed);

            optimizer.optimizeParameters(numberOfTrials);
        }
    }
}
