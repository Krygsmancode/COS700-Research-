package com.example.trongp.GP;

import com.example.trongp.FitnessPlotter;
import java.util.Random;

public class GPFramework {
    private Population redPopulation;
    private Population bluePopulation;
    private int generations;
    private Random random;

    public GPFramework(int populationSize, int generations, int seed) {
        this.random = new Random(seed);
        this.redPopulation = new Population(populationSize, GPParameters.MAX_DEPTH,  random); 
        this.bluePopulation = new Population(populationSize, GPParameters.MAX_DEPTH, random);
        this.generations = generations;
    }

    public static Agent bestRedAgentOverall = null;
    public static Agent bestBlueAgentOverall = null;
    
    public void runEvolution() {
        for (int generation = 1; generation <= generations; generation++) {
            System.out.println("Generation " + generation + " in progress...");
    
            // Evaluate the fitness of the populations
            redPopulation.evaluateFitness(bluePopulation);
            bluePopulation.evaluateFitness(redPopulation);
    
            // Select the best agents for this generation
            Agent bestRedAgentThisGen = redPopulation.getBestAgent();
            Agent bestBlueAgentThisGen = bluePopulation.getBestAgent();
    
            // Check if they are better than the overall best
            if (bestRedAgentOverall == null || bestRedAgentThisGen.getFitness() > bestRedAgentOverall.getFitness()) {
                bestRedAgentOverall = bestRedAgentThisGen;
            }
    
            if (bestBlueAgentOverall == null || bestBlueAgentThisGen.getFitness() > bestBlueAgentOverall.getFitness()) {
                bestBlueAgentOverall = bestBlueAgentThisGen;
            }
    
            // Output the tree structure of the best individuals
            System.out.println("Best Red Agent Tree: " + bestRedAgentOverall.getStrategy().getTreeRepresentation());
            System.out.println("Best Blue Agent Tree: " + bestBlueAgentOverall.getStrategy().getTreeRepresentation());
    
            // Update fitness plot
            FitnessPlotter.updatePlot(generation, bestRedAgentOverall.getFitness(), bestBlueAgentOverall.getFitness());
    
            // Evolve the populations
            redPopulation.evolve();
            bluePopulation.evolve();
        }
    
        // Output the final best agents
        System.out.println("\nFinal Best Red Agent: " + bestRedAgentOverall.getNumber() + " Fitness: " + bestRedAgentOverall.getFitness());
        System.out.println("Final Best Blue Agent: " + bestBlueAgentOverall.getNumber() + " Fitness: " + bestBlueAgentOverall.getFitness());
    }
    

    public Agent getBestRedAgent() {
        return redPopulation.getBestAgent();
    }

    public Agent getBestBlueAgent() {
        return bluePopulation.getBestAgent();
    }
}
