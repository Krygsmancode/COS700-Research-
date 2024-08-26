package com.example.trongp.GP;

import com.example.trongp.FitnessPlotter;

public class EvolutionEngine {
    private Population redPopulation;
    private Population bluePopulation;
    private int generations;
    private Agent bestRedAgent;
    private Agent bestBlueAgent;

    public EvolutionEngine(int populationSize, int generations) {
        this.redPopulation = new Population(populationSize, GPParameters.MAX_DEPTH);
        this.bluePopulation = new Population(populationSize, GPParameters.MAX_DEPTH);
        this.generations = generations;
    }

    public void runEvolution() {
        for (int generation = 1; generation <= generations; generation++) {
            System.out.println("Generation " + generation + " in progress...");

            // Evaluate fitness for both populations
            redPopulation.evaluateFitness(bluePopulation);
            bluePopulation.evaluateFitness(redPopulation);

            // Evolve populations
            redPopulation.evolve();
            bluePopulation.evolve();

            // Track the best agents
            bestRedAgent = redPopulation.getBestAgent();
            bestBlueAgent = bluePopulation.getBestAgent();

            // Log and plot fitness information
            double avgRedFitness = redPopulation.calculateAverageFitness();
            double avgBlueFitness = bluePopulation.calculateAverageFitness();
            double maxRedFitness = redPopulation.calculateMaxFitness();
            double maxBlueFitness = bluePopulation.calculateMaxFitness();
            double varianceRedFitness = redPopulation.calculateFitnessVariance();
            double varianceBlueFitness = bluePopulation.calculateFitnessVariance();

            FitnessPlotter.updatePopulationStats(generation, 
                (avgRedFitness + avgBlueFitness) / 2,
                Math.max(maxRedFitness, maxBlueFitness),
                (varianceRedFitness + varianceBlueFitness) / 2);

            FitnessPlotter.updatePlot(generation, bestRedAgent.getFitness(), bestBlueAgent.getFitness());
        }
    }

    public Agent getBestRedAgent() {
        return bestRedAgent;
    }

    public Agent getBestBlueAgent() {
        return bestBlueAgent;
    }
}
