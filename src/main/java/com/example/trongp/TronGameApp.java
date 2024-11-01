package com.example.trongp;

import java.io.IOException;

public class TronGameApp {
    public static void main(String[] args) {
        // Create an instance of the ParameterOptimizer
        ParameterOptimizer optimizer = new ParameterOptimizer();

        // Define the number of trials for optimization
        int numberOfTrials = 50; // Adjust this number based on how extensive you want the search to be

        // Run the parameter optimization process
        optimizer.optimizeParameters(numberOfTrials);

        // The optimizer now handles the evolution and saving of results internally
        // Any further code here can be for final clean-up or additional logging if necessary

        // Keep the application running if necessary (for GUIs or other reasons)
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
