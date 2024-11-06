package com.example.trongp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.EvolutionEngine;
import com.example.trongp.GP.GPParameters;

public class ParameterOptimizer {
    private Random random;
    private int seed;

    private List<OptimizationResult> results = new ArrayList<>();


    public ParameterOptimizer(Random random, int seed) {
        this.random = random;
        this.seed = seed;
    }

    private void saveResults(EvolutionEngine engine, String outputDir, int trialNumber) {
        Agent bestRedAgent = engine.getBestRedAgent();
        Agent bestBlueAgent = engine.getBestBlueAgent();

        // Update the csvFilename to include the outputDir
        String csvFilename = outputDir + "/optimization_results.csv"; // CSV file for each trial

        try (CSVSaver csvSaver = new CSVSaver(csvFilename)) {
            csvSaver.saveRun(bestRedAgent, bestBlueAgent, seed, trialNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




public void optimizeParameters(int trials) {
    
    double bestFitness = Double.NEGATIVE_INFINITY;
    Map<String, Object> bestParameters = new HashMap<>();
    Agent bestAgent = null; // To store the best agent

    // Create seed directory
    String seedDir = "Seed_" + seed;
    File seedDirectory = new File(seedDir);
    if (!seedDirectory.exists()) {
        seedDirectory.mkdir();
    }

    for (int i = 0; i < trials; i++) {
        // Use the existing random instance for generating parameters
        GPParameters.MUTATION_RATE = 0.01 + 0.09 * random.nextDouble();
        GPParameters.PHASE2_MUTATION_RATE = 0.01 + 0.09 * random.nextDouble();
        GPParameters.CROSSOVER_RATE = 0.6 + 0.4 * random.nextDouble();
        GPParameters.TOURNAMENT_SIZE = 3 + random.nextInt(8);
        GPParameters.phase1MaxDepth = 2 + random.nextInt(4);
        GPParameters.phase2MaxDepth = 5 + random.nextInt(4);


        // Create a unique identifier for each trial
        int trialNumber = i + 1;
        String trialId = "Trial_" + trialNumber;
        String trialDir = seedDir + "/" + trialId;
        File dir = new File(trialDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Update filenamePrefix for EvolutionEngine
        String filenamePrefix = trialDir + "/EvolutionEngine";

        // Run the evolution engine with these parameters
        EvolutionEngine engine = new EvolutionEngine(
            GPParameters.POPULATION_SIZE,
            GPParameters.GENERATIONS,
            filenamePrefix,
            random // Pass the shared Random instance
        );       
         engine.runEvolution(seed, trialNumber);

         saveResults(engine, trialDir, trialNumber);

        // Get the fitness histories
        List<Double> bestFitnessHistorySolo = engine.getBestFitnessHistorySolo();
        List<Double> averageFitnessHistorySolo = engine.getAverageFitnessHistorySolo();
        List<Double> bestFitnessHistoryRed = engine.getBestFitnessHistoryRed();
        List<Double> averageFitnessHistoryRed = engine.getAverageFitnessHistoryRed();
        List<Double> bestFitnessHistoryBlue = engine.getBestFitnessHistoryBlue();
        List<Double> averageFitnessHistoryBlue = engine.getAverageFitnessHistoryBlue();

        // Save the fitness plots
        saveFitnessPlot(bestFitnessHistorySolo, averageFitnessHistorySolo, trialDir + "/Phase1_FitnessPlot");
        saveFitnessPlot(bestFitnessHistoryRed, averageFitnessHistoryRed, trialDir + "/RedPopulation_FitnessPlot");
        saveFitnessPlot(bestFitnessHistoryBlue, averageFitnessHistoryBlue, trialDir + "/BluePopulation_FitnessPlot");

        // Get the best fitness achieved
        Agent currentBestAgent = engine.getBestRedAgent(); // Assuming best red agent
        double currentBestFitness = currentBestAgent.getFitness();
        double currentAverageFitness = engine.getRedPopulation().getMeanFitness();
        double currentVarianceFitness = engine.getRedPopulation().getFitnessVariance();

        // Save the parameters and fitness
        Map<String, Object> currentParameters = new HashMap<>();
        currentParameters.put("MUTATION_RATE", GPParameters.MUTATION_RATE);
        currentParameters.put("PHASE2_MUTATION_RATE", GPParameters.PHASE2_MUTATION_RATE);
        currentParameters.put("CROSSOVER_RATE", GPParameters.CROSSOVER_RATE);
        currentParameters.put("POPULATION_SIZE", GPParameters.POPULATION_SIZE);
        currentParameters.put("TOURNAMENT_SIZE", GPParameters.TOURNAMENT_SIZE);
        currentParameters.put("phase1MaxDepth", GPParameters.phase1MaxDepth);
        currentParameters.put("phase2MaxDepth", GPParameters.phase2MaxDepth);

        OptimizationResult result = new OptimizationResult(currentParameters, currentBestFitness, currentAverageFitness, currentVarianceFitness);
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
        saveResultsToFile(seedDir + "/optimization_results.csv");
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

    // In ParameterOptimizer.java

private void saveFitnessPlot(List<Double> bestFitnessHistory, List<Double> averageFitnessHistory, String filename) {
    XYSeries bestFitnessSeries = new XYSeries("Best Fitness");
    XYSeries averageFitnessSeries = new XYSeries("Average Fitness");

    for (int i = 0; i < bestFitnessHistory.size(); i++) {
        bestFitnessSeries.add(i + 1, bestFitnessHistory.get(i));
        averageFitnessSeries.add(i + 1, averageFitnessHistory.get(i));
    }

    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(bestFitnessSeries);
    dataset.addSeries(averageFitnessSeries);

    JFreeChart chart = ChartFactory.createXYLineChart(
            "Fitness Over Generations",
            "Generation",
            "Fitness",
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,
            true,
            false
    );

    // Ensure the directory exists
    File file = new File(filename + ".png");
    file.getParentFile().mkdirs();

    try {
        ChartUtils.saveChartAsPNG(file, chart, 800, 600);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// In ParameterOptimizer.java

public EvolutionEngine getRedPopulation() {
    return getRedPopulation();
}

public EvolutionEngine getBluePopulation() {
    return getBluePopulation();
}



    // In ParameterOptimizer.java

    private void saveResultsToFile(String filename) {
        boolean fileExists = new File(filename).exists();
        try (FileWriter writer = new FileWriter(filename, true)) {
            // Write header if file does not exist
            if (!fileExists) {
                writer.append("Trial,Seed,MUTATION_RATE,PHASE2_MUTATION_RATE,CROSSOVER_RATE,POPULATION_SIZE,TOURNAMENT_SIZE,phase1MaxDepth,phase2MaxDepth,BestFitness,AverageFitness,FitnessVariance\n");
            }
            int trialNumber = 1;
            for (OptimizationResult result : results) {
                writer.append(String.valueOf(trialNumber)).append(",");
                writer.append(String.valueOf(seed)).append(",");
                writer.append(String.valueOf(result.parameters.get("MUTATION_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("PHASE2_MUTATION_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("CROSSOVER_RATE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("POPULATION_SIZE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("TOURNAMENT_SIZE"))).append(",");
                writer.append(String.valueOf(result.parameters.get("phase1MaxDepth"))).append(",");
                writer.append(String.valueOf(result.parameters.get("phase2MaxDepth"))).append(",");
                writer.append(String.valueOf(result.bestFitness)).append(",");
                writer.append(String.valueOf(result.averageFitness)).append(",");
                writer.append(String.valueOf(result.varianceFitness)).append("\n");
                trialNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


   // In ParameterOptimizer.java

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

// ...existing code...

}
