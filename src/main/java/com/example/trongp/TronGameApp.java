package com.example.trongp;

import java.io.IOException;
import java.util.Random;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.EvolutionEngine;
import com.example.trongp.GP.GPParameters;

public class TronGameApp {
    public static void main(String[] args) {

        EvolutionEngine engine = new EvolutionEngine(
            GPParameters.POPULATION_SIZE,
            GPParameters.GENERATIONS

        );
        engine.runEvolution();

        // Get the best agents after evolution
        Agent bestRedAgent = engine.getBestRedAgent();
        Agent bestBlueAgent = engine.getBestBlueAgent();



        // Save the results using CSVSaver
        try (CSVSaver csvSaver = new CSVSaver("results.csv")) {
            csvSaver.saveRun(bestRedAgent, bestBlueAgent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Keep the application running until the user closes the graph windows
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
