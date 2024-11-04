package com.example.trongp;

import java.io.IOException;
import java.util.Random;

import com.example.trongp.GP.GPParameters;

// In TronGameApp.java

public class TronGameApp {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true"); // Run in headless mode

        int seed = GPParameters.SEED; // Use the fixed seed from GPParameters
        Random random = new Random(seed); // Initialize Random with the seed

        // Create an instance of the ParameterOptimizer with the random instance
        ParameterOptimizer optimizer = new ParameterOptimizer(random);

        int numberOfTrials = 10; // Adjust this number based on your needs
        optimizer.optimizeParameters(numberOfTrials);
    }
}

