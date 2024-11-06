package com.example.trongp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;
import com.example.trongp.GP.Agent;
import com.example.trongp.GP.GPParameters;

public class CSVSaver implements AutoCloseable {
    private PrintWriter writer;

    public CSVSaver(String filePath) throws IOException {
        File file = new File(filePath);

        // Ensure the directory exists for the provided file path
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        boolean fileExists = file.exists();
        
        writer = new PrintWriter(new FileWriter(file, true)); // Append mode

        if (!fileExists) {
            writeHeader();
        }
    }

    private void writeHeader() {
        writer.println("Seed,Trial,Population Size,Phase1 Max Depth,Phase2 Max Depth,Mutation Rate,Phase2 Mutation Rate,Tournament Size,Crossover Rate,Grid Size,Generations,Games to Play,Win Weight,Trail Weight,Best Red Agent Fitness,Best Blue Agent Fitness");
    }

    public void saveRun(Agent bestRedAgent, Agent bestBlueAgent, int seed, int trialNumber) {
        writer.printf(Locale.US, "%d,%d,%d,%d,%d,%.4f,%.4f,%d,%.4f,%d,%d,%d,%.4f,%.4f,%.3f,%.3f%n",
                seed,
                trialNumber,
                GPParameters.POPULATION_SIZE,
                GPParameters.phase1MaxDepth,
                GPParameters.phase2MaxDepth,
                GPParameters.MUTATION_RATE,
                GPParameters.PHASE2_MUTATION_RATE,
                GPParameters.TOURNAMENT_SIZE,
                GPParameters.CROSSOVER_RATE,
                GPParameters.GRID_SIZE,
                GPParameters.GENERATIONS,
                GPParameters.GAMES_TO_PLAY,
                GPParameters.WIN_WEIGHT,
                GPParameters.TRAIL_WEIGHT,
                bestRedAgent.getFitness(),
                bestBlueAgent.getFitness()
        );
    }

    public void savePopulationMetrics(String filePath, int seed, int trialNumber, Map<String, Double> metrics) throws IOException {
        File file = new File(filePath);

        // Ensure the directory exists for the provided file path
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        boolean fileExists = file.exists();
    
        try (PrintWriter metricWriter = new PrintWriter(new FileWriter(file, true))) {
            if (!fileExists) {
                metricWriter.println("Seed,Trial,WinRate,LossRate,DrawRate,AverageTrailLength,MaxTrailLength,MinTrailLength,TrailLengthStdDev");
            }
    
            metricWriter.printf(Locale.US, "%d,%d,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f%n",
                    seed, trialNumber,
                    metrics.getOrDefault("WinRate", 0.0),
                    metrics.getOrDefault("LossRate", 0.0),
                    metrics.getOrDefault("DrawRate", 0.0),
                    metrics.getOrDefault("AverageTrailLength", 0.0),
                    metrics.getOrDefault("MaxTrailLength", 0.0),
                    metrics.getOrDefault("MinTrailLength", 0.0),
                    metrics.getOrDefault("TrailLengthStdDev", 0.0)
            );
        }
    }
    
    @Override
    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
