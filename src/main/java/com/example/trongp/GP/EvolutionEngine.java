package com.example.trongp.GP;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.trongp.FitnessPlotter;
import com.example.trongp.GameState;

public class EvolutionEngine {
    private Population population;
    private Population redPopulation;
    private Population bluePopulation;
    private int generations;
    private boolean isSoloPhase;
    private FitnessPlotter fitnessPlotter;
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
    private List<Double> worstFitnessHistoryRed;
private List<Double> worstFitnessHistoryBlue;
private List<Double> worstFitnessHistorySolo;


private List<Map<Integer, Integer>> featureUsageHistoryRed;
private List<Map<Integer, Integer>> featureUsageHistoryBlue;
private List<Map<Integer, Integer>> featureUsageHistorySolo;

private String filenamePrefix;



    // Removed nextAgentId as it's no longer needed in the solo phase
    // private int nextAgentId = -10;

    public EvolutionEngine(int populationSize, int generations, String filenamePrefix, Random random) {
        this.random = random;
        this.generations = generations;
        this.isSoloPhase = true; // Start with solo phase
        this.filenamePrefix = filenamePrefix;



    //    System.out.println("Initializing Evolution Engine with population size: " + populationSize);
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

        // Initialize in constructor
featureUsageHistoryRed = new ArrayList<>();
featureUsageHistoryBlue = new ArrayList<>();
featureUsageHistorySolo = new ArrayList<>();

        

        // Initialize the fitness plotter
        fitnessPlotter = new FitnessPlotter(filenamePrefix);
    }

    public void runEvolution() {
        // Phase 1: Solo Phase
  //      System.out.println("Starting Phase 1: Solo Phase");
        for (int generation = 1; generation <= GPParameters.PHASE1_GENERATIONS; generation++) {
    //        System.out.println("Generation " + generation + " in progress (Solo Phase)...");

            // Evaluate fitness for the solo phase
            population.evaluateFitness(null);
            Map<Integer, Integer> featureCountsSolo = population.getFeatureUsageCounts();
            featureUsageHistorySolo.add(featureCountsSolo);

            if (generation % GPParameters.PHASE1_GENERATIONS == 0) {
                Map<Integer, Integer> cumulativeFeatureCountsSolo = aggregateFeatureUsage(featureUsageHistorySolo);
                fitnessPlotter.plotFeatureUsageHeatmap(cumulativeFeatureCountsSolo, "Solo Population Feature Usage - Generation " + generation);
            }
        

            // Log best agent's tree representation
            Agent bestSoloAgent = population.getBestAgent();
     //       System.out.println("Best Solo Agent (Generation " + generation + "):");
      //      System.out.println("Fitness: " + bestSoloAgent.getFitness());
      //      System.out.println("Strategy Tree:\n" + bestSoloAgent.getStrategy().getTreeRepresentation());

            // Collect and plot fitness data for the solo phase
            collectFitnessData(population, bestFitnessHistorySolo, averageFitnessHistorySolo, fitnessVarianceHistorySolo);
            fitnessPlotter.updatePhase1Plot(generation, population.getBestFitness(), population.getMeanFitness(), population.getFitnessVariance());

            // Evolve population for solo phase
            population.evolve(null);
            logPopulationDetails(population, "Post-Evolution " + generation, true);

        }

        simulateBestSoloAgent();

        System.out.println("Phase 1 completed. Transitioning to Phase 2...");
        preparePhase2Populations(); // This method prepares and transitions to competitive phase

        // Phase 2: Competitive Phase
        System.out.println("Starting Phase 2: Competitive Phase");
        for (int generation = GPParameters.PHASE1_GENERATIONS + 1; generation <= GPParameters.GENERATIONS; generation++) {
        //    System.out.println("Generation " + generation + " in progress (Competitive Phase)...");

            // Evaluate fitness for competitive phase
            redPopulation.evaluateFitness(bluePopulation);
            bluePopulation.evaluateFitness(redPopulation);
            Map<Integer, Integer> featureCountsRed = redPopulation.getFeatureUsageCounts();
            Map<Integer, Integer> featureCountsBlue = bluePopulation.getFeatureUsageCounts();
            featureUsageHistoryRed.add(featureCountsRed);
            featureUsageHistoryBlue.add(featureCountsBlue);

            if (generation % GPParameters.GENERATIONS == 0) {
                Map<Integer, Integer> cumulativeFeatureCountsRed = aggregateFeatureUsage(featureUsageHistoryRed);
                Map<Integer, Integer> cumulativeFeatureCountsBlue = aggregateFeatureUsage(featureUsageHistoryBlue);
        
                fitnessPlotter.plotFeatureUsageHeatmap(cumulativeFeatureCountsRed, "Red Population Feature Usage - Generation " + generation);
                fitnessPlotter.plotFeatureUsageHeatmap(cumulativeFeatureCountsBlue, "Blue Population Feature Usage - Generation " + generation);
            }

            // Log best agents' tree representations
            Agent bestRedAgent = redPopulation.getBestAgent();
            Agent bestBlueAgent = bluePopulation.getBestAgent();
     //       System.out.println("Best Red Agent (Generation " + generation + "):");
     //       System.out.println("Fitness: " + bestRedAgent.getFitness());
      //      System.out.println("Strategy Tree:\n" + bestRedAgent.getStrategy().getTreeRepresentation());

      //      System.out.println("Best Blue Agent (Generation " + generation + "):");
     //       System.out.println("Fitness: " + bestBlueAgent.getFitness());
    //        System.out.println("Strategy Tree:\n" + bestBlueAgent.getStrategy().getTreeRepresentation());
//
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
            // System.out.println("After evolution of Generation " + generation + ":");
            // System.out.println("Red Population:");
            // System.out.println("  Best Fitness: " + redPopulation.getBestFitness());
            // System.out.println("  Mean Fitness: " + redPopulation.getMeanFitness());
            // System.out.println("Blue Population:");
            // System.out.println("  Best Fitness: " + bluePopulation.getBestFitness());
            // System.out.println("  Mean Fitness: " + bluePopulation.getMeanFitness());

            
        }


        simulateBestAgentsGame(); // Existing method
        compareTopAgentsWithHandCrafted();

        
fitnessPlotter.savePhase1Plot(filenamePrefix + "_Phase1_FitnessPlot");
fitnessPlotter.savePhase2Plot(filenamePrefix + "_Phase2_FitnessPlot");

    }

    private Map<Integer, Integer> aggregateFeatureUsage(List<Map<Integer, Integer>> featureUsageHistory) {
        Map<Integer, Integer> aggregatedCounts = new HashMap<>();
        for (Map<Integer, Integer> featureCounts : featureUsageHistory) {
            for (Map.Entry<Integer, Integer> entry : featureCounts.entrySet()) {
                int featureIndex = entry.getKey();
                int count = entry.getValue();
                aggregatedCounts.put(featureIndex, aggregatedCounts.getOrDefault(featureIndex, 0) + count);
            }
        }
        return aggregatedCounts;
    }
    

    private void logPopulationDetails(Population population, String stage, boolean printTrees) {
    //    System.out.println(stage + " Details:");
        Agent bestAgent = null;
        double bestFitness = Double.MIN_VALUE;

        for (Agent agent : population.getAgents()) {
   //         System.out.println("Agent #" + agent.getNumber() + " - Fitness: " + agent.getFitness());
            if (printTrees) {
   //             System.out.println("Strategy Tree for Agent #" + agent.getNumber() + ":\n" + agent.getStrategy().getTreeRepresentation());
            }
            if (agent.getFitness() > bestFitness) {
                bestFitness = agent.getFitness();
                bestAgent = agent;
            }
        }

        if (bestAgent != null) {
   //         System.out.println("Best Agent's Decision Tree:");
   //         System.out.println(bestAgent.getStrategy().getTreeRepresentation());
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

    private void simulateBestSoloAgent() {
        Agent bestSoloAgent = population.getBestAgent().cloneWithNewNumber(1);

        System.out.println("\nSimulating run of the best solo agent:");
        System.out.println("Agent ID: " + bestSoloAgent.getId());
        System.out.println("Fitness: " + bestSoloAgent.getFitness());
        System.out.println("Strategy Tree:\n" + bestSoloAgent.getStrategy().getTreeRepresentation());

        // Initialize a new game state in solo mode
        GameState gameState = new GameState(GPParameters.SOLO_GRID_SIZE, GPParameters.SOLO_GRID_SIZE, true, random);

        // Simulate the agent's moves
        while (!gameState.isGameOver()) {
            int move = bestSoloAgent.makeMove(gameState, bestSoloAgent.getNumber());
            if (move == -1) {
       //         System.out.println("Agent has no valid moves. Game over.");
                break;
            }
            gameState.update(move, -1); // Update game state with agent's move
          //  gameState.printGrid(); // Print the grid to visualize the agent's path
            // try {
            //     Thread.sleep(500); // Pause for half a second between moves for better visualization
            // } catch (InterruptedException e) {
            //     Thread.currentThread().interrupt();
            // }
        }

     //   System.out.println("Final Grid State:");
    //    gameState.printGrid();
        gameState.displayGridCoverage();
    }

    // In EvolutionEngine.java

    public void compareTopAgentsWithHandCrafted() {
        System.out.println("\nComparing top agents with the hand-crafted agent...");
    
        // Get top N agents from red and blue populations
        int numberOfAgents = 5;
        List<Agent> topRedAgents = redPopulation.getTopNAgents(numberOfAgents);
        List<Agent> topBlueAgents = bluePopulation.getTopNAgents(numberOfAgents);
    
        // Create a hand-crafted agent
        HandCraftedAgent handCraftedAgent = new HandCraftedAgent(2, this.random); // Agent number 2 for opponent
    
        // Number of games to play
        int gamesToPlay = 100; // Set a fixed number of games
    
        // Collect statistics
        Map<String, Double> redStats = simulateAgentsAgainstHandCrafted(topRedAgents, handCraftedAgent, gamesToPlay);
        Map<String, Double> blueStats = simulateAgentsAgainstHandCrafted(topBlueAgents, handCraftedAgent, gamesToPlay);
    
        // Present results
        System.out.println("\n--- Results Against Hand-Crafted Agent ---");
        System.out.println("Red Agents vs Hand-Crafted Agent:");
        displayStats(redStats);
    
        System.out.println("Blue Agents vs Hand-Crafted Agent:");
        displayStats(blueStats);
    
        // Save results to file
        saveComparisonResults("comparison_results.csv", redStats, blueStats);
    }

    private void saveComparisonResults(String filename, Map<String, Double> redStats, Map<String, Double> blueStats) {
    try (FileWriter writer = new FileWriter(filename)) {
        writer.write("Comparison Results Against Hand-Crafted Agent\n");
        writer.write("Metric,Red Agents,Blue Agents\n");
        for (String key : redStats.keySet()) {
            writer.write(String.format("%s,%.4f,%.4f\n", key, redStats.get(key), blueStats.get(key)));
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    // Method to simulate multiple games between GP agents and a hand-crafted agent
    private Map<String, Double> simulateAgentsAgainstHandCrafted(List<Agent> agents, HandCraftedAgent handCraftedAgent, int gamesToPlay) {
        int totalGames = gamesToPlay * agents.size();
        int totalWins = 0;
        int totalLosses = 0;
        int totalDraws = 0;
        int totalTrailLength = 0;
        int maxTrailLength = Integer.MIN_VALUE;
        int minTrailLength = Integer.MAX_VALUE;
        List<Integer> trailLengths = new ArrayList<>();

        for (Agent agent : agents) {
            for (int i = 0; i < gamesToPlay; i++) {
                // Clone agents to ensure consistency
                Agent clonedAgent = agent.cloneWithNewNumber(1); // Agent number 1 for our GP agent
                HandCraftedAgent clonedHandCraftedAgent = (HandCraftedAgent) handCraftedAgent.cloneWithNewNumber(2); // Agent number 2 for the hand-crafted agent

                // Initialize a new game state
                GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE, false, this.random);

                // Simulate the game until it's over
                while (!gameState.isGameOver()) {
                    int moveAgent = clonedAgent.makeMove(gameState, clonedAgent.getNumber());
                    int moveHandCrafted = clonedHandCraftedAgent.makeMove(gameState, clonedHandCraftedAgent.getNumber());
                    gameState.update(moveAgent, moveHandCrafted);
                }

                // Determine the outcome
                boolean agentWin = gameState.didAgentWin(clonedAgent);
                boolean handCraftedWin = gameState.didAgentWin(clonedHandCraftedAgent);

                if (agentWin && !handCraftedWin) {
                    totalWins++;
                } else if (!agentWin && handCraftedWin) {
                    totalLosses++;
                } else {
                    totalDraws++;
                }

                // Calculate trail length
                int trailLength = gameState.calculateTrailLength(clonedAgent);
                totalTrailLength += trailLength;
                trailLengths.add(trailLength);
                if (trailLength > maxTrailLength) {
                    maxTrailLength = trailLength;
                }
                if (trailLength < minTrailLength) {
                    minTrailLength = trailLength;
                }
            }
        }

        // Calculate statistics
        double averageTrailLength = (double) totalTrailLength / totalGames;
        double trailLengthVariance = calculateVariance(trailLengths, averageTrailLength);
        double trailLengthStdDev = Math.sqrt(trailLengthVariance);

        Map<String, Double> stats = new HashMap<>();
        stats.put("WinRate", (double) totalWins / totalGames);
        stats.put("LossRate", (double) totalLosses / totalGames);
        stats.put("DrawRate", (double) totalDraws / totalGames);
        stats.put("AverageTrailLength", averageTrailLength);
        stats.put("MaxTrailLength", (double) maxTrailLength);
        stats.put("MinTrailLength", (double) minTrailLength);
        stats.put("TrailLengthStdDev", trailLengthStdDev);

        return stats;
    }

private double calculateVariance(List<Integer> values, double mean) {
    double variance = 0.0;
    for (double value : values) {
        variance += Math.pow(value - mean, 2);
    }
    return variance / values.size();
}

private void displayStats(Map<String, Double> stats) {
    System.out.printf("  Win Rate: %.2f%%\n", stats.get("WinRate") * 100);
    System.out.printf("  Loss Rate: %.2f%%\n", stats.get("LossRate") * 100);
  //  System.out.printf("  Draw Rate: %.2f%%\n", stats.get("DrawRate") * 100);
    System.out.printf("  Average Trail Length: %.2f\n", stats.get("AverageTrailLength"));
    System.out.printf("  Max Trail Length: %.2f\n", stats.get("MaxTrailLength"));
    System.out.printf("  Min Trail Length: %.2f\n", stats.get("MinTrailLength"));
    System.out.printf("  Trail Length Std Dev: %.2f\n", stats.get("TrailLengthStdDev"));
}

public List<Double> getBestFitnessHistoryRed() {
    return bestFitnessHistoryRed;
}

public List<Double> getAverageFitnessHistoryRed() {
    return averageFitnessHistoryRed;
}

public List<Double> getBestFitnessHistoryBlue() {
    return bestFitnessHistoryBlue;
}

public List<Double> getAverageFitnessHistoryBlue() {
    return averageFitnessHistoryBlue;
}

public List<Double> getBestFitnessHistorySolo() {
    return bestFitnessHistorySolo;
}

public List<Double> getAverageFitnessHistorySolo() {
    return averageFitnessHistorySolo;
}

// Add getters for the populations
public Population getRedPopulation() {
    return redPopulation;
}

public Population getBluePopulation() {
    return bluePopulation;
}



    private void simulateBestAgentsGame() {
        Agent bestRed = redPopulation.getBestAgent().cloneWithNewNumber(1);
        Agent bestBlue = bluePopulation.getBestAgent().cloneWithNewNumber(2);

        System.out.println("\nSimulating game between best agents:");
        System.out.println("Best Red Agent #" + bestRed.getNumber() + " Fitness: " + bestRed.getFitness());
        System.out.println("Best Blue Agent #" + bestBlue.getNumber() + " Fitness: " + bestBlue.getFitness());

        System.out.println("Red Agent Strategy Tree:\n" + bestRed.getStrategy().getTreeRepresentation());
        System.out.println("Blue Agent Strategy Tree:\n" + bestBlue.getStrategy().getTreeRepresentation());

        GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE, false, random);

        while (!gameState.isGameOver()) {
            int moveRed = bestRed.makeMove(gameState, bestRed.getNumber());
            int moveBlue = bestBlue.makeMove(gameState, bestBlue.getNumber());
            gameState.update(moveRed, moveBlue);

            // Optionally, print the game state after each move
            // gameState.printGrid();
        }

        if (gameState.didAgentWin(bestRed)) {
        //    System.out.println("Best Red Agent wins!");
        } else if (gameState.didAgentWin(bestBlue)) {
         //   System.out.println("Best Blue Agent wins!");
        } else {
        //    System.out.println("It's a draw!");
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
        int numBestAgents = totalSize / 2; // 50% of the existing agents are selected based on their fitness
        int numNewAgents = totalSize - numBestAgents; // 50% of the population will be newly created agents
    
        // Sort agents by fitness in descending order
        List<Agent> sortedAgents = new ArrayList<>(population.getAgents());
        sortedAgents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());
    
        // Select top 50% agents
        List<Agent> selectedAgents = new ArrayList<>(sortedAgents.subList(0, numBestAgents));
    
        // Increase tree depth for selected agents to allow adaptation
        for (Agent agent : selectedAgents) {
            agent.getStrategy().adjustTreeDepth(GPParameters.phase2MaxDepth);
            agent.getStrategy().setPhase(true); // Ensure isPhase2 is set to true
        }
    
   // Generate new agents with full feature set
List<Agent> newAgents = new ArrayList<>();
for (int i = 0; i < numNewAgents; i++) {
    Strategy randomStrategy = new Strategy(GPParameters.phase2MaxDepth, random, false, true); // isPhase2 = true
    Agent newAgent = new Agent(randomStrategy, -1,random); // Placeholder agent number
    newAgents.add(newAgent);
}

    
        // Combine selected and new agents
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
        redPopulation = new Population(redAgents, GPParameters.phase2MaxDepth, random, false, 1);
        bluePopulation = new Population(blueAgents, GPParameters.phase2MaxDepth, random, false, 2);
    
        isSoloPhase = false; // Transition to competitive phase
    }
    
}
