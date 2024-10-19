package com.example.trongp.GP;

import com.example.trongp.FitnessPlotter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GPFramework {
    private Population population;
    private Population redPopulation;
    private Population bluePopulation;
    private Random random;
    private boolean isSoloPhase;

    public static Agent bestRedAgentOverall = null;
    public static Agent bestBlueAgentOverall = null;
    public GPFramework(int populationSize, int maxDepth, Random random, boolean isSoloPhase) {
        this.random = random;
        this.isSoloPhase = isSoloPhase;

        if (isSoloPhase) {
            // Initialize one population for the solo phase
            this.population = new Population(populationSize, maxDepth, random, true);
        } else {
            // Initialize two populations for the competitive phase
            this.redPopulation = new Population(populationSize / 2, maxDepth, random, false);
            this.bluePopulation = new Population(populationSize / 2, maxDepth, random, false);
        }
    }


    public void runEvolution() {
        if (isSoloPhase) {
            // Evolve single population
            population.evaluateFitnessSolo();
            population.evolve(population);
        } else {
            // Evolve both red and blue populations competitively
            redPopulation.evaluateFitness(bluePopulation);
            bluePopulation.evaluateFitness(redPopulation);
            redPopulation.evolve(bluePopulation);
            bluePopulation.evolve(redPopulation);
        }
    }

    private void updateBestAgents(Agent redAgent, Agent blueAgent) {
        if (bestRedAgentOverall == null || redAgent.getFitness() > bestRedAgentOverall.getFitness()) {
            bestRedAgentOverall = redAgent;
        }
        if (bestBlueAgentOverall == null || blueAgent.getFitness() > bestBlueAgentOverall.getFitness()) {
            bestBlueAgentOverall = blueAgent;
        }
    }

    private void printBestAgents() {
        System.out.println("Best Red Agent Tree: " + bestRedAgentOverall.getStrategy().getTreeRepresentation());
        System.out.println("Best Blue Agent Tree: " + bestBlueAgentOverall.getStrategy().getTreeRepresentation());
    }

    private void printFinalBestAgents() {
        System.out.println("\nFinal Best Red Agent: " + bestRedAgentOverall.getNumber() + " Fitness: " + bestRedAgentOverall.getFitness());
        System.out.println("Final Best Blue Agent: " + bestBlueAgentOverall.getNumber() + " Fitness: " + bestBlueAgentOverall.getFitness());
    }

    public Agent getBestRedAgent() {
        return redPopulation.getBestAgent();
    }

    public Agent getBestBlueAgent() {
        return bluePopulation.getBestAgent();
    }


public void splitPopulation() {
    // Sort agents from Phase 1 by fitness
    List<Agent> sortedAgents = new ArrayList<>(population.getAgents());
    sortedAgents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());

    int totalSize = sortedAgents.size();
    int existingAgentsCount = (int) (0.9 * totalSize);
    int newAgentsCount = totalSize - existingAgentsCount;

    // Create new populations with existing agents
    List<Agent> redAgents = new ArrayList<>();
    List<Agent> blueAgents = new ArrayList<>();
    Random rand = new Random();

    for (int i = 0; i < existingAgentsCount / 2; i++) {
        redAgents.add(sortedAgents.get(i).clone());
    }
    for (int i = existingAgentsCount / 2; i < existingAgentsCount; i++) {
        blueAgents.add(sortedAgents.get(i).clone());
    }

    // Create new agents from scratch
    for (int i = 0; i < newAgentsCount / 2; i++) {
        Strategy newStrategy = new Strategy(rand.nextInt(GPParameters.phase2MaxDepth - 2) + 2, random, false); // Min depth of 2
        redAgents.add(new Agent(newStrategy, population.getNextAgentId()));
    }
    for (int i = newAgentsCount / 2; i < newAgentsCount; i++) {
        Strategy newStrategy = new Strategy(rand.nextInt(GPParameters.phase2MaxDepth - 2) + 2, random, false); // Min depth of 2
        blueAgents.add(new Agent(newStrategy, population.getNextAgentId()));
    }

    // Initialize new populations
    redPopulation = new Population(redAgents, GPParameters.phase2MaxDepth, random, false);
    bluePopulation = new Population(blueAgents, GPParameters.phase2MaxDepth, random, false);

    isSoloPhase = false;  // Switch to competitive phase
}


}