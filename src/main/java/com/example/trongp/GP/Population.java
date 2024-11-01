package com.example.trongp.GP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import com.example.trongp.GameState;

public class Population {
    private List<Agent> agents;
    private Random random;
    private boolean isSoloPhase;
    private int maxDepth;
    private double elitismRate;
    private int agentNumberForNewAgents; // New field to assign agent numbers



    public Population(int populationSize, int maxDepth, Random random, boolean isSoloPhase) {
        this.random = random;
        this.maxDepth = maxDepth;
        this.elitismRate = GPParameters.elitismRate;
        this.isSoloPhase = isSoloPhase;
        this.agentNumberForNewAgents = isSoloPhase ? 1 : -1; // -1 as placeholder for competitive phase
        agents = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            Strategy strategy = new Strategy(maxDepth, random, true, !isSoloPhase); // isPhase2 = !isSoloPhase
            Agent agent = new Agent(strategy, agentNumberForNewAgents);
            agents.add(agent);
        }
    }
    
    
    public Population(List<Agent> agents, int maxDepth, Random random, boolean isSoloPhase, int agentNumberForNewAgents) {
        this.random = random;
        this.maxDepth = maxDepth;
        this.agents = new ArrayList<>(agents);
        this.isSoloPhase = isSoloPhase;
        this.elitismRate = GPParameters.elitismRate;
        this.agentNumberForNewAgents = agentNumberForNewAgents;
    }

    public double calculateAverageTreeDistance() {
        int totalDistance = 0;
        int comparisons = 0;
    
        for (int i = 0; i < agents.size(); i++) {
            for (int j = i + 1; j < agents.size(); j++) {
                totalDistance += agents.get(i).getStrategy().calculateTreeDistance(agents.get(j).getStrategy());
                comparisons++;
            }
        }
    
        return comparisons == 0 ? 0 : (double) totalDistance / comparisons;
    }

    public void analyzePopulationDynamics() {
        double averageTreeDistance = calculateAverageTreeDistance();
        double meanFitness = getMeanFitness();
        double fitnessVariance = getFitnessVariance();
        double fitnessStdDev = getFitnessStandardDeviation();
        double averageHammingDistance = calculateAverageHammingDistance();
    
        System.out.println("Population Dynamics:");
        System.out.println("  Average Tree Distance: " + averageTreeDistance);
        System.out.println("  Average Hamming Distance: " + averageHammingDistance);
        System.out.println("  Mean Fitness: " + meanFitness);
        System.out.println("  Fitness Variance: " + fitnessVariance);
        System.out.println("  Fitness Standard Deviation: " + fitnessStdDev);
    }
    
    
    
    

    private double calculateAverageHammingDistance() {
        int totalDistance = 0;
        int comparisons = 0;
    
        for (int i = 0; i < agents.size(); i++) {
            for (int j = i + 1; j < agents.size(); j++) {
                totalDistance += calculateHammingDistance(agents.get(i).getStrategy(), agents.get(j).getStrategy());
                comparisons++;
            }
        }
    
        return comparisons == 0 ? 0 : (double) totalDistance / comparisons;
        
    }

    private int calculateHammingDistance(Strategy strategy, Strategy strategy2) {
        Node root1 = strategy.getRoot();
        Node root2 = strategy2.getRoot();
        return calculateNodeDistance(root1, root2);
    }

    private int calculateNodeDistance(Node root1, Node root2) {
        if (root1 == null && root2 == null) {
            return 0;
        } else if (root1 == null || root2 == null) {
            return 1;
        }
    
        int distance = (root1.getClass() != root2.getClass()) ? 1 : 0;
    
        if (root1 instanceof DecisionNode && root2 instanceof DecisionNode) {
            DecisionNode decision1 = (DecisionNode) root1;
            DecisionNode decision2 = (DecisionNode) root2;
    
            if (decision1.getDecisionFeature() != decision2.getDecisionFeature()) {
                distance++;
            }
            if (decision1.getThreshold() != decision2.getThreshold()) {
                distance++;
            }
    
            distance += calculateNodeDistance(decision1.getLeft(), decision2.getLeft());
            distance += calculateNodeDistance(decision1.getRight(), decision2.getRight());
        }
    
        return distance;
    }

    public void adjustDepth(int newMaxDepth) {
        boolean isPhase2 = !this.isSoloPhase; // Determine current phase
        for (Agent agent : agents) {
            Strategy strategy = agent.getStrategy();
            strategy.setMaxDepth(newMaxDepth);
            strategy.regenerateStrategy(newMaxDepth, isPhase2);  // Pass isPhase2 here
        }
    }
    
    

    public void setSoloPhase(boolean isSoloPhase) {
        this.isSoloPhase = isSoloPhase;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    // Combined evaluation for both solo and competitive phases, based on isSoloPhase flag
// In Population.java
public void evaluateFitness(Population opponentPopulation) {
    if (opponentPopulation == null) {
        // Solo Phase
        evaluateFitnessSolo();
    } else {
        // Competitive Phase
        evaluateFitnessCompetitive(opponentPopulation);
    }
}

public void evolve(Population opponentPopulation) {
    List<Agent> newAgents = new ArrayList<>();
    int eliteCount = (int) (elitismRate * agents.size());

    // Sort agents by fitness in descending order
    agents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());

    // Preserve elite agents
    for (int i = 0; i < eliteCount; i++) {
        Agent eliteAgent = agents.get(i).clone();
        eliteAgent.setElite(true); // Mark the cloned agent as elite
        newAgents.add(eliteAgent);
    }

    // Generate new offspring
    while (newAgents.size() < agents.size()) {
        Agent parent1 = selectAgent();
        Agent parent2;
        do {
            parent2 = selectAgent();
        } while (parent1 == parent2);

        Strategy offspringStrategy = parent1.getStrategy().crossover(parent2.getStrategy(), maxDepth);
        boolean isPhase2 = !isSoloPhase;

        // Use increased mutation rate and adjusted feature selection in Phase 2
        offspringStrategy.mutate(maxDepth);

        Agent offspring = new Agent(offspringStrategy, agentNumberForNewAgents);
        offspring.setElite(false);

        // Evaluate offspring fitness
        if (isSoloPhase) {
            evaluateFitnessSoloForAgent(offspring);
        } else {
            evaluateFitnessCompetitiveForAgent(offspring, opponentPopulation);
        }

        newAgents.add(offspring);
    }

    agents = newAgents;
}

    
    
    public void evaluateFitnessSolo() {
        for (Agent a : agents) {
            // Skip re-evaluation if the agent is elite (no changes in strategy)
            if (!a.isElite()) {
                evaluateFitnessSoloForAgent(a);
            }
          //  applyFitnessSharing();

            analyzePopulationDynamics();
        }
    }
    
    public void evaluateFitnessCompetitive(Population opponentPopulation) {
        for (Agent a : agents) {
            evaluateFitnessCompetitiveForAgent(a, opponentPopulation);
        }

        //applyFitnessSharing();

    
        // Analyze population dynamics after fitness evaluation
        analyzePopulationDynamics();
    
        // Debugging: Print fitness statistics
        System.out.println("Phase 2 Population Fitness Statistics:");
        System.out.println("  Best Fitness: " + getBestFitness());
        System.out.println("  Mean Fitness: " + getMeanFitness());
        System.out.println("  Fitness Variance: " + getFitnessVariance());
        System.out.println("  Fitness Standard Deviation: " + getFitnessStandardDeviation());
    }


   private void evaluateFitnessCompetitiveForAgent(Agent agent, Population opponentPopulation) {
    int wins = 0;
    int draws = 0;
    int losses = 0;
    double totalTrailLength = 0.0;
    double fitness = 0.0;

    HandCraftedAgent handCraftedAgent = new HandCraftedAgent(2); // Assuming agent number 2


    for (int i = 0; i < GPParameters.GAMES_TO_PLAY; i++) {
        Agent opponent;

        if (i % 2 == 0) {
            // Every other game, play against the handcrafted agent
            opponent = handCraftedAgent;
        } else {
            // Otherwise, select a random opponent from the opponent population
            opponent = opponentPopulation.selectRandomAgent();
        }
        
        // Initialize a new game state with agents randomly placed
        GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE, false);
        
        // Reset the game to ensure random starting positions
        gameState.reset();
        
        // Run the game simulation
        int result = simulateCompetition(agent, opponent, gameState);
        
        // Track results (wins, losses, draws)
        if (result == 1) {
            wins++;
        } else if (result == 0) {
            draws++;
        } else if (result == -1) {
            losses++;
        }

        // Calculate trail length
        totalTrailLength += gameState.calculateTrailLength(agent);
    }

    // Calculate fitness based on game outcomes and trail length
    double winRate = (double) wins / GPParameters.GAMES_TO_PLAY;
    double drawRate = (double) draws / GPParameters.GAMES_TO_PLAY;
    double lossRate = (double) losses / GPParameters.GAMES_TO_PLAY;

    fitness += (winRate * GPParameters.WIN_WEIGHT);
    fitness += (drawRate * GPParameters.DRAW_WEIGHT);
    fitness -= (lossRate * GPParameters.LOSS_PENALTY);
    if (wins > 0) {
        fitness += (totalTrailLength / wins) * GPParameters.TRAIL_WEIGHT;
    }
    // Ensure fitness doesn't go negative
    fitness = Math.max(fitness, 0.0);
    agent.setFitness(fitness);
    
    // Debugging output
    System.out.println("Agent #" + agent.getNumber() + " Competitive Fitness Calculated: " + fitness);
}
private void evaluateFitnessSoloForAgent(Agent agent) {
    GameState gameStateSolo = new GameState(GPParameters.SOLO_GRID_SIZE, GPParameters.SOLO_GRID_SIZE, true);
    double initialFitness = agent.getFitness();
    System.out.println("Starting Fitness Evaluation for Agent #" + agent.getNumber() + " Initial Fitness: " + initialFitness);

    int steps = 0;
    double fitness = 0.0;
    boolean hitWall = false;
    Set<Point> uniqueCellsVisited = new HashSet<>();

    while (!gameStateSolo.isGameOver()) {
        int move = agent.makeMove(gameStateSolo, 1);
        gameStateSolo.update(move, -1);  // Ensure this method's logic is correct
        steps++;

        if (gameStateSolo.hasCollisionOccurred()) {
            hitWall = true;
            break;
        }

        // Assuming unique cell visits increase fitness
        int agentX = (int) gameStateSolo.getAgent1X();
        int agentY = (int) gameStateSolo.getAgent1Y();
        Point currentPosition = new Point(agentX, agentY);
        uniqueCellsVisited.add(currentPosition);
    }

    // Calculate grid coverage
    int totalCells = GPParameters.SOLO_GRID_SIZE * GPParameters.SOLO_GRID_SIZE;
    double coverage = (double) uniqueCellsVisited.size() / totalCells;

    // Apply grid coverage reward
    fitness += coverage * GPParameters.FULL_GRID_BONUS;  // FULL_GRID_BONUS to be defined in GPParameters

    // Apply exploration and revisiting penalties
    fitness += uniqueCellsVisited.size() * GPParameters.EXPLORATION_WEIGHT;
    fitness -= (steps - uniqueCellsVisited.size()) * GPParameters.REVISIT_PENALTY_WEIGHT;

    // Collision penalties
    if (hitWall) {
        fitness -= GPParameters.LOSS_PENALTY * (GPParameters.SOLO_MAX_STEPS - steps);
    }

    fitness = Math.max(fitness, 0.0);
    agent.setFitness(fitness);
    System.out.println("Completed Fitness Evaluation for Agent #" + agent.getNumber() + " Final Fitness: " + fitness);
}

    
    
    private double calculateExplorationBonus(GameState gameState, Agent agent) {
        int[][] grid = gameState.getGrid();
        int uniqueCellsCovered = 0;
    
        // Count unique cells
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] == agent.getNumber()) uniqueCellsCovered++;
            }
        }
    
        double explorationBonus = uniqueCellsCovered * GPParameters.EXPLORATION_WEIGHT;
        return explorationBonus;
    }
    

    private int getUniqueCellsCovered(GameState gameState, Agent agent) {
        int[][] grid = gameState.getGrid();
        int uniqueCellsCovered = 0;
    
        // Count how many unique cells the agent has covered
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == agent.getNumber()) uniqueCellsCovered++;
            }
        }
        return uniqueCellsCovered;
    }




    
    private double calculateRevisitPenalty(GameState gameState, Agent agent) {
        int agentNumber = agent.getNumber();
        int revisitCount = 0;
        List<Point> agentPositions = gameState.getAgentTrail(agentNumber);
    
        if (agentPositions == null || agentPositions.isEmpty()) {
            return 0.0;
        }
    
        Set<Point> visitedPoints = new HashSet<>();
        for (Point position : agentPositions) {
            if (!visitedPoints.add(position)) {
                revisitCount++;
            }
        }
    
        // Apply a penalty for each revisit
        return revisitCount * GPParameters.REVISIT_PENALTY_WEIGHT;
    }
    private int simulateCompetition(Agent agent1, Agent agent2, GameState gameState) {
        // Clone agents and assign agent numbers 1 and 2
        Agent redAgent = (agent1 instanceof HandCraftedAgent) ? agent1 : agent1.cloneWithNewNumber(1);
        Agent blueAgent = (agent2 instanceof HandCraftedAgent) ? agent2 : agent2.cloneWithNewNumber(2);
        
        // Print decision trees for both agents before the competition begins
        // System.out.println("Agent #" + redAgent.getNumber() + " Decision Tree:\n" + redAgent.getStrategy().getRoot().toString());
        // System.out.println("Agent #" + blueAgent.getNumber() + " Decision Tree:\n" + blueAgent.getStrategy().getRoot().toString());
    
        while (!gameState.isGameOver()) {
            int move1 = redAgent.makeMove(gameState, redAgent.getNumber());
            int move2 = blueAgent.makeMove(gameState, blueAgent.getNumber());
    
            // Log the moves
            System.out.println("Agent #" + redAgent.getNumber() + " Move: " + move1);
            System.out.println("Agent #" + blueAgent.getNumber() + " Move: " + move2);
    
            // Update the game state with both moves
            gameState.update(move1, move2);
        }
    
        // Determine who won or lost based on the final game state
        if (gameState.didAgentWin(redAgent)) {
            return 1; // Red agent wins
        } else if (gameState.didAgentWin(blueAgent)) {
            return -1; // Blue agent wins
        } else {
            // Handle edge cases where no clear winner is decided (e.g., both agents collide)
            System.out.println("Game over with no clear winner. Resolving based on remaining safe moves.");
    
            // Check if agents have safe moves left to decide the winner
            boolean redHasMoves = hasSafeMoves(gameState, redAgent);
            boolean blueHasMoves = hasSafeMoves(gameState, blueAgent);
    
            if (redHasMoves && !blueHasMoves) {
                return 1; // Red agent wins by having more safe moves
            } else if (!redHasMoves && blueHasMoves) {
                return -1; // Blue agent wins by having more safe moves
            } else {
                return 1; // Agent 1 wins by default if both are tied
            }
        }
    }
    
    
    private boolean hasSafeMoves(GameState gameState, Agent agent) {
        // Get the current position of the agent
        java.awt.Point currentPosition = gameState.getCurrentPosition(agent.getNumber());
    
        if (currentPosition == null) {
            System.out.println("Error: Agent #" + agent.getNumber() + " does not have a valid current position.");
            return false;
        }
    
        // Loop through all possible moves (0: up, 1: down, 2: left, 3: right)
        for (int move = 0; move < 4; move++) {
            com.example.trongp.Point nextPosition = agent.getNextPosition(currentPosition.x, currentPosition.y, move);
            
            // Check if the next position is safe
            if (nextPosition != null && gameState.isPositionSafe(nextPosition.x, nextPosition.y)) {
                // Safe move found
                return true;
            }
        }
        
        // No safe moves found
        return false;
    }

    private Agent selectRandomAgent() {
        return agents.get(random.nextInt(agents.size()));
    }
    
    
    

    private Agent selectAgent() {
        List<Agent> tournament = new ArrayList<>();
        for (int i = 0; i < GPParameters.TOURNAMENT_SIZE; i++) {
            Agent candidate = agents.get(random.nextInt(agents.size()));
            tournament.add(candidate);
        }
        return Collections.max(tournament, Comparator.comparingDouble(Agent::getFitness));
    }
    
    public Agent getBestAgent() {
        return Collections.max(agents, Comparator.comparingDouble(Agent::getFitness));
    }
    

    public double size() {
        return agents.size();
       
    }

    // In Population.java, during fitness evaluation
private void applyFitnessSharing() {
    for (Agent agent : agents) {
        double sharedFitness = agent.getFitness() / (1 + countSimilarAgents(agent));
        agent.setFitness(sharedFitness);
    }
}

private int countSimilarAgents(Agent targetAgent) {
    int count = 0;
    for (Agent agent : agents) {
        if (agent != targetAgent && isSimilar(agent, targetAgent)) {
            count++;
        }
    }
    return count;
}

private boolean isSimilar(Agent agent1, Agent agent2) {
    // Define similarity based on strategy tree distance or behavior
    int distance = agent1.getStrategy().calculateTreeDistance(agent2.getStrategy());
    return distance < GPParameters.SIMILARITY_THRESHOLD;
}


    public double getMeanFitness() {
        double totalFitness = 0.0;
        for (Agent agent : agents) {
            totalFitness += agent.getFitness();
        }
        return totalFitness / agents.size();
    }
    
    public double getFitnessVariance() {
        double mean = getMeanFitness();
        double variance = 0.0;
        for (Agent agent : agents) {
            variance += Math.pow(agent.getFitness() - mean, 2);
        }
        return variance / agents.size();
    }
    
    public double getFitnessStandardDeviation() {
        return Math.sqrt(getFitnessVariance());
    }




    public double getBestFitness() {
      
        return getBestAgent().getFitness();
    }

    public Map<Integer, Integer> getFeatureUsageCounts() {
    Map<Integer, Integer> featureCounts = new HashMap<>();

    for (Agent agent : agents) {
        traverseTreeAndCountFeatures(agent.getStrategy().getRoot(), featureCounts);
    }

    return featureCounts;
}

private void traverseTreeAndCountFeatures(Node node, Map<Integer, Integer> featureCounts) {
    if (node instanceof DecisionNode) {
        DecisionNode decisionNode = (DecisionNode) node;
        int featureIndex = decisionNode.decisionFeature;
        featureCounts.put(featureIndex, featureCounts.getOrDefault(featureIndex, 0) + 1);

        traverseTreeAndCountFeatures(decisionNode.left, featureCounts);
        traverseTreeAndCountFeatures(decisionNode.right, featureCounts);
    }
}





public List<Agent> getTopNAgents(int n) {
    // Sort agents by fitness in descending order
    agents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());
    // Return the top N agents
    return new ArrayList<>(agents.subList(0, Math.min(n, agents.size())));
}



    
}
