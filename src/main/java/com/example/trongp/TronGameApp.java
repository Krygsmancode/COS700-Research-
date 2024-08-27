package com.example.trongp;

import com.example.trongp.GP.Agent;
import com.example.trongp.GP.GPFramework;
import com.example.trongp.GP.GPParameters;
import com.example.trongp.GP.NodeFactory;
import com.example.trongp.GP.Population;
import java.io.IOException;
import java.util.Random;
import com.example.trongp.CSVSaver;

public class TronGameApp {
    public static void main(String[] args) {
        // Seed for reproducibility
        Random random = new Random(GPParameters.SEED);

        // Initialize parameters
        int populationSize = GPParameters.POPULATION_SIZE;
        int generations = GPParameters.GENERATIONS;
        int maxDepth = GPParameters.MAX_DEPTH;

        // Initialize NodeFactory with the seed for reproducibility
        NodeFactory.setRandomSeed(random);

        // Initialize two populations for red and blue agents
        Population redPopulation = new Population(populationSize, maxDepth, random);
        Population bluePopulation = new Population(populationSize, maxDepth, random);

        // Evaluate fitness and run the evolutionary process
        redPopulation.evaluateFitness(bluePopulation);
        bluePopulation.evaluateFitness(redPopulation);

        GPFramework gpFramework = new GPFramework(populationSize, generations, GPParameters.SEED);
        gpFramework.runEvolution();

        // Get the best agents after evolution
        Agent bestRedAgent = gpFramework.getBestRedAgent();
        Agent bestBlueAgent = gpFramework.getBestBlueAgent();

        // Save relevant data to CSV
        try (CSVSaver csvSaver = new CSVSaver("results.csv")) {
            csvSaver.saveRun(GPParameters.SEED, bestRedAgent, bestBlueAgent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("\nBest Red Agent Fitness: " + bestRedAgent.getFitness());
        System.out.println("Best Blue Agent Fitness: " + bestBlueAgent.getFitness());
    }
}
