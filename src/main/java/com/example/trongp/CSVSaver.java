package com.example.trongp;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.GPParameters;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSVSaver implements AutoCloseable {  // Implement AutoCloseable
    private PrintWriter writer;

    public CSVSaver(String fileName) throws IOException {
        writer = new PrintWriter(new FileWriter(fileName, true)); // Append mode
        writeHeader();
    }

    private void writeHeader() {
        writer.println("Seed,Population Size,Max Depth,Mutation Rate,Tournament Size,Crossover Rate,Grid Size,Generations,Games to Play,Win Weight,Trail Weight,Best Red Agent Fitness,Best Blue Agent Fitness");
    }

    public void saveRun(int seed, Agent bestRedAgent, Agent bestBlueAgent) {
        writer.printf("%d,%d,%d,%.2f,%d,%.2f,%d,%d,%d,%.2f,%.2f,%.2f,%.2f%n",
                seed,
                GPParameters.POPULATION_SIZE,
                GPParameters.MAX_DEPTH,
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
