package com.example.trongp.GP;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import com.example.trongp.FitnessPlotter;
import com.example.trongp.GameState;

public class EvolutionEngine {
    private Population population;
    private Population redPopulation;
    private Population bluePopulation;
    private int generations;
    private boolean isSoloPhase;
    private com.example.trongp.FitnessPlotter fitnessPlotter;
    private Random random;
    private List<Double> bestFitnessHistoryRed;
private List<Double> averageFitnessHistoryRed;
private List<Double> bestFitnessHistoryBlue;
private List<Double> averageFitnessHistoryBlue;
private List<Double> bestFitnessHistorySolo;
private List<Double> averageFitnessHistorySolo;
private List<Double> fitnessVarianceHistoryRed;
private List<Double> fitnessVarianceHistoryBlue;
private List<Double> fitnessVarianceHistorySolo;


private int nextAgentId = -10;

public EvolutionEngine(int populationSize, int generations) {
    this.random = new Random();
    // Initialize populations with phase 1 max depth
    population = new Population(populationSize, GPParameters.phase1MaxDepth, random, true);

    bestFitnessHistoryRed = new ArrayList<>();
    averageFitnessHistoryRed = new ArrayList<>();
    fitnessVarianceHistoryRed = new ArrayList<>();

    bestFitnessHistoryBlue = new ArrayList<>();
    averageFitnessHistoryBlue = new ArrayList<>();
    fitnessVarianceHistoryBlue = new ArrayList<>();

    bestFitnessHistorySolo = new ArrayList<>();
    averageFitnessHistorySolo = new ArrayList<>();
    fitnessVarianceHistorySolo = new ArrayList<>();

    // Initialize the fitness plotter
    fitnessPlotter = new FitnessPlotter();
}

public void runEvolution() {
    // Phase 1: Solo Phase
    System.out.println("Starting Phase 1: Solo Phase");
    for (int generation = 1; generation <= GPParameters.PHASE1_GENERATIONS; generation++) {
        System.out.println("Generation " + generation + " in progress (Solo Phase)...");

        // Evaluate fitness for the solo phase
        population.evaluateFitness(null);

        // Log best agent's tree representation
        Agent bestSoloAgent = population.getBestAgent();
        System.out.println("Best Solo Agent (Generation " + generation + "):");
        System.out.println("Fitness: " + bestSoloAgent.getFitness());
        System.out.println("Strategy Tree:\n" + bestSoloAgent.getStrategy().getTreeRepresentation());

        // Collect and plot fitness data for the solo phase
        collectFitnessData(population, bestFitnessHistorySolo, averageFitnessHistorySolo, fitnessVarianceHistorySolo);
        fitnessPlotter.updatePhase1Plot(generation, population.getBestFitness(), population.getMeanFitness(), population.getFitnessVariance());

        // Evolve populations for solo phase
        population.evolve(null);
        logPopulationDetails(population, "Post-Evolution " + generation, true);
    }

    System.out.println("Phase 1 completed. Transitioning to Phase 2...");
    preparePhase2Populations(); // This method prepares and transitions to competitive phase

    // Phase 2: Competitive Phase
    System.out.println("Starting Phase 2: Competitive Phase");
    for (int generation = GPParameters.PHASE1_GENERATIONS + 1; generation <= GPParameters.GENERATIONS; generation++) {
        System.out.println("Generation " + generation + " in progress (Competitive Phase)...");

        // Evaluate fitness for competitive phase
        redPopulation.evaluateFitness(bluePopulation);
        bluePopulation.evaluateFitness(redPopulation);

        // Log best agents' tree representations
        Agent bestRedAgent = redPopulation.getBestAgent();
        Agent bestBlueAgent = bluePopulation.getBestAgent();
        System.out.println("Best Red Agent (Generation " + generation + "):");
        System.out.println("Fitness: " + bestRedAgent.getFitness());
        System.out.println("Strategy Tree:\n" + bestRedAgent.getStrategy().getTreeRepresentation());

        System.out.println("Best Blue Agent (Generation " + generation + "):");
        System.out.println("Fitness: " + bestBlueAgent.getFitness());
        System.out.println("Strategy Tree:\n" + bestBlueAgent.getStrategy().getTreeRepresentation());

        // Collect and plot fitness data for competitive phase
        collectFitnessData(redPopulation, bestFitnessHistoryRed, averageFitnessHistoryRed, fitnessVarianceHistoryRed);
        collectFitnessData(bluePopulation, bestFitnessHistoryBlue, averageFitnessHistoryBlue, fitnessVarianceHistoryBlue);
        fitnessPlotter.updatePhase2Plot(generation,
            redPopulation.getBestFitness(), bluePopulation.getBestFitness(),
            redPopulation.getMeanFitness(), bluePopulation.getMeanFitness(),
            redPopulation.getFitnessVariance(), bluePopulation.getFitnessVariance());

        // Evolve populations competitively
        redPopulation.evolve(bluePopulation);
        bluePopulation.evolve(redPopulation);

        // Optionally, log details after evolution
        System.out.println("After evolution of Generation " + generation + ":");
        System.out.println("Red Population:");
        System.out.println("  Best Fitness: " + redPopulation.getBestFitness());
        System.out.println("  Mean Fitness: " + redPopulation.getMeanFitness());
        System.out.println("Blue Population:");
        System.out.println("  Best Fitness: " + bluePopulation.getBestFitness());
        System.out.println("  Mean Fitness: " + bluePopulation.getMeanFitness());
    }
}


private void logPopulationDetails(Population population, String stage, boolean printTrees) {
    System.out.println(stage + " Details:");
    Agent bestAgent = null;
    double bestFitness = Double.MIN_VALUE;

    for (Agent agent : population.getAgents()) {
        System.out.println("Agent #" + agent.getNumber() + " - Fitness: " + agent.getFitness());
        if (printTrees) {
            System.out.println("Strategy Tree for Agent #" + agent.getNumber() + ":\n" + agent.getStrategy().getTreeRepresentation());
        }
        if (agent.getFitness() > bestFitness) {
            bestFitness = agent.getFitness();
            bestAgent = agent;
        }
    }

    if (bestAgent != null) {
        System.out.println("Best Agent's Decision Tree:");
        System.out.println(bestAgent.getStrategy().getTreeRepresentation());
    }
}



private void collectFitnessData(Population population, List<Double> bestFitnessHistory,
                                List<Double> averageFitnessHistory, List<Double> fitnessVarianceHistory) {
    bestFitnessHistory.add(population.getBestFitness());
    averageFitnessHistory.add(population.getMeanFitness());
    fitnessVarianceHistory.add(population.getFitnessVariance());

    // Debugging: Print collected fitness data
    System.out.println("Collected Fitness Data:");
    System.out.println("  Best Fitness: " + population.getBestFitness());
    System.out.println("  Mean Fitness: " + population.getMeanFitness());
    System.out.println("  Fitness Variance: " + population.getFitnessVariance());
}



 
private void simulateBestAgentsGame() {
    Agent bestRed = redPopulation.getBestAgent().cloneWithNewNumber(1);
    Agent bestBlue = bluePopulation.getBestAgent().cloneWithNewNumber(2);

    System.out.println("\nSimulating game between best agents:");
    System.out.println("Best Red Agent #" + bestRed.getNumber() + " Fitness: " + bestRed.getFitness());
    System.out.println("Best Blue Agent #" + bestBlue.getNumber() + " Fitness: " + bestBlue.getFitness());

    System.out.println("Red Agent Strategy Tree:\n" + bestRed.getStrategy().getTreeRepresentation());
    System.out.println("Blue Agent Strategy Tree:\n" + bestBlue.getStrategy().getTreeRepresentation());

    GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE, false);

    while (!gameState.isGameOver()) {
        int moveRed = bestRed.makeMove(gameState, bestRed.getNumber());
        int moveBlue = bestBlue.makeMove(gameState, bestBlue.getNumber());
        gameState.update(moveRed, moveBlue);

        // Optionally, print the game state after each move
        // gameState.printGrid();
    }

    if (gameState.didAgentWin(bestRed)) {
        System.out.println("Best Red Agent wins!");
    } else if (gameState.didAgentWin(bestBlue)) {
        System.out.println("Best Blue Agent wins!");
    } else {
        System.out.println("It's a draw!");
    }
}


public Agent getBestRedAgent() {
    return redPopulation != null ? redPopulation.getBestAgent() : null;
}

public Agent getBestBlueAgent() {
    return bluePopulation != null ? bluePopulation.getBestAgent() : null;
}
private void preparePhase2Populations() {
    int totalSize = population.getAgents().size();
    int numBestAgents = (int) (0.7 * totalSize); // 90% of the existing agents are selected based on their fitness
    int numNewAgents = totalSize - numBestAgents; // 10% of the population will be newly created agents

    // Sort agents by fitness in descending order
    List<Agent> sortedAgents = new ArrayList<>(population.getAgents());
    sortedAgents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());

    // Use a LinkedHashMap to maintain insertion order and ensure uniqueness based on strategy representation
    Map<String, Agent> uniqueAgentsMap = new LinkedHashMap<>();
    for (Agent agent : sortedAgents) {
        String strategyRepresentation = agent.getStrategy().getTreeRepresentation();
        if (!uniqueAgentsMap.containsKey(strategyRepresentation)) {
            uniqueAgentsMap.put(strategyRepresentation, agent);
        }
        if (uniqueAgentsMap.size() >= numBestAgents) {
            break; // Stop once we have the desired number of unique agents
        }
    }

    List<Agent> selectedAgents = new ArrayList<>(uniqueAgentsMap.values());

    // Create new agents to introduce genetic diversity
    List<Agent> newAgents = new ArrayList<>();
    for (int i = 0; i < numNewAgents; i++) {
        Strategy randomStrategy = new Strategy(GPParameters.phase2MaxDepth, random, false);
        Agent newAgent = new Agent(randomStrategy, Population.getNextAgentId());
        newAgents.add(newAgent);
    }

    // Combine the selected and new agents
    List<Agent> combinedAgents = new ArrayList<>();
    combinedAgents.addAll(selectedAgents);
    combinedAgents.addAll(newAgents);

    // Shuffle combined agents to randomize their distribution
    Collections.shuffle(combinedAgents, random);

    // Split the combined agents into two halves for red and blue populations
    int halfSize = combinedAgents.size() / 2;
    List<Agent> redAgents = new ArrayList<>();
    List<Agent> blueAgents = new ArrayList<>();

    for (int i = 0; i < combinedAgents.size(); i++) {
        Agent originalAgent = combinedAgents.get(i);

        // Clone the agent to ensure each has its own unique strategy
        Agent clonedAgent = originalAgent.clone();

        if (i < halfSize) {
            clonedAgent.setNumber(1); // Assign agent number 1 to red agents
            redAgents.add(clonedAgent); // Add to red population
        } else {
            clonedAgent.setNumber(2); // Assign agent number 2 to blue agents
            blueAgents.add(clonedAgent); // Add to blue population
        }
    }

    // Initialize new populations for the competitive phase
    redPopulation = new Population(redAgents, GPParameters.phase2MaxDepth, random, false);
    bluePopulation = new Population(blueAgents, GPParameters.phase2MaxDepth, random, false);

    isSoloPhase = false; // Transition to competitive phase
}


public int getNextAgentId() {
    return nextAgentId++;
}





}
