package com.example.trongp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.GPParameters;

public class CSVSaver implements AutoCloseable {
    private PrintWriter writer;

    public CSVSaver(String fileName) throws IOException {
        File file = new File(fileName);
        boolean fileExists = file.exists();
        
        writer = new PrintWriter(new FileWriter(file, true)); // Append mode

        if (!fileExists) {
            writeHeader();
        }
    }

    private void writeHeader() {
        writer.println("Seed,Population Size,Max Depth,Mutation Rate,Tournament Size,Crossover Rate,Grid Size,Generations,Games to Play,Win Weight,Trail Weight,Best Red Agent Fitness,Best Blue Agent Fitness");
    }

    public void saveRun(Agent bestRedAgent, Agent bestBlueAgent) {
        writer.printf(Locale.US, "%d,%d,%d,%.2f,%d,%.2f,%d,%d,%d,%.2f,%.2f,%.3f,%.3f%n",
                GPParameters.SEED,
                GPParameters.POPULATION_SIZE,
                GPParameters.phase1MaxDepth,
                GPParameters.MUTATION_RATE,
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

    @Override
    public void close() {
        writer.close();
    }
}
