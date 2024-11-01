package com.example.trongp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.EvolutionEngine;
import com.example.trongp.GP.GPParameters;

public class ParameterOptimizer {
    private Random random = new Random();
    private List<OptimizationResult> results = new ArrayList<>();

    public void optimizeParameters(int trials) {
        double bestFitness = Double.NEGATIVE_INFINITY;
        Map<String, Object> bestParameters = new HashMap<>();
        Agent bestAgent = null; // To store the best agent

        for (int i = 0; i < trials; i++) {
            // Randomly select parameters
            GPParameters.MUTATION_RATE = random.nextDouble(); // 0.0 to 1.0
            GPParameters.PHASE2_MUTATION_RATE = random.nextDouble(); // 0.0 to 1.0
            GPParameters.CROSSOVER_RATE = 0.6 + random.nextDouble() / 2; // 0.5 to 1.0
            GPParameters.POPULATION_SIZE = 20 + random.nextInt(181); // 20 to 200
            GPParameters.TOURNAMENT_SIZE = 2 + random.nextInt(7); // 2 to 10
            GPParameters.phase1MaxDepth = 2 + random.nextInt(5); // 2 to 6
            GPParameters.phase2MaxDepth = 4 + random.nextInt(7); // 4 to 10

            // Run the evolution engine with these parameters
            EvolutionEngine engine = new EvolutionEngine(GPParameters.POPULATION_SIZE, GPParameters.GENERATIONS);
            engine.runEvolution();

            // Get the best fitness achieved
            Agent currentBestAgent = engine.getBestRedAgent(); // Assuming best red agent
            double currentBestFitness = currentBestAgent.getFitness();

            // Save the parameters and fitness
            Map<String, Object> currentParameters = new HashMap<>();
            currentParameters.put("MUTATION_RATE", GPParameters.MUTATION_RATE);
            currentParameters.put("PHASE2_MUTATION_RATE", GPParameters.PHASE2_MUTATION_RATE);
            currentParameters.put("CROSSOVER_RATE", GPParameters.CROSSOVER_RATE);
            currentParameters.put("POPULATION_SIZE", GPParameters.POPULATION_SIZE);
            currentParameters.put("TOURNAMENT_SIZE", GPParameters.TOURNAMENT_SIZE);
            currentParameters.put("phase1MaxDepth", GPParameters.phase1MaxDepth);
            currentParameters.put("phase2MaxDepth", GPParameters.phase2MaxDepth);

            OptimizationResult result = new OptimizationResult(currentParameters, currentBestFitness);
            results.add(result);

            // If this is the best so far, save the parameters and agent
            if (currentBestFitness > bestFitness) {
                bestFitness = currentBestFitness;
                bestParameters = new HashMap<>(currentParameters);
                bestAgent = currentBestAgent.clone(); // Clone to avoid mutation in future iterations
            }

            // Optionally, print the progress
            System.out.println("Trial " + (i + 1) + "/" + trials + " completed.");
            System.out.println("Current Best Fitness: " + currentBestFitness);
            System.out.println("Best Fitness so far: " + bestFitness);

            // Save the current result to file after each trial
            saveResultsToFile("optimization_results.csv");
        }

        // Output the best parameters found
        System.out.println("Optimization completed.");
        System.out.println("Best Fitness Achieved: " + bestFitness);
        System.out.println("Best Parameters:");
        for (Map.Entry<String, Object> entry : bestParameters.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        // Save the best result to a separate file
        saveBestResultToFile("best_parameters.txt", bestParameters, bestFitness, bestAgent);
    }

    private void saveResultsToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            // Write header
            writer.append("Trial,MUTATION_RATE,PHASE2_MUTATION_RATE,CROSSOVER_RATE,POPULATION_SIZE,TOURNAMENT_SIZE,phase1MaxDepth,phase2MaxDepth,BestFitness\n");
            int trialNumber = 1;
            for (OptimizationResult result : results) {
                writer.append(String.valueOf(trialNumber)).append(",");
                writer.append(String.valueOf(result.parameters.get("MUTATION_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("PHASE2_MUTATION_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("CROSSOVER_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("POPULATION_SIZE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("TOURNAMENT_SIZE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("phase1MaxDepth"))).append(",");
                writer.append(String.valueOf(result.parameters.get("phase2MaxDepth"))).append(",");
                writer.append(String.valueOf(result.bestFitness)).append("\n");
                trialNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBestResultToFile(String filename, Map<String, Object> bestParameters, double bestFitness, Agent bestAgent) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Best Fitness Achieved: " + bestFitness + "\n");
            writer.write("Best Parameters:\n");
            for (Map.Entry<String, Object> entry : bestParameters.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            writer.write("\nBest Agent Strategy Tree:\n");
            writer.write(bestAgent.getStrategy().getTreeRepresentation());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
