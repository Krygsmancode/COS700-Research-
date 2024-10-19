package com.example.trongp.GP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
    private static int nextAgentId = 1;  // Static to ensure it is shared across instances


    public Population(int size, int maxDepth, Random random, boolean isSoloPhase) {
        this.random = random;
        this.maxDepth = maxDepth;
        this.agents = new ArrayList<>();
        this.elitismRate = GPParameters.elitismRate;
    
        this.isSoloPhase = isSoloPhase;
        for (int i = 0; i < size; i++) {
            Strategy randomStrategy;
            if (i < size / 2) {
                randomStrategy = new Strategy(maxDepth, random, true); // Use full method
            } else {
                randomStrategy = new Strategy(maxDepth, random, false); // Use grow method
            }
            agents.add(new Agent(randomStrategy, nextAgentId++));
            System.out.println("Agent #" + nextAgentId + " created with strategy: " + (i < size / 2 ? "full" : "grow"));

        }
        
    }

    public Population(List<Agent> agents, int maxDepth, Random random, boolean isSoloPhase) {
        this.random = random;
        this.maxDepth = maxDepth;
        this.agents = new ArrayList<>(agents);
        this.isSoloPhase = isSoloPhase;
        this.elitismRate = GPParameters.elitismRate;
    
        int maxId = agents.stream().mapToInt(Agent::getNumber).max().orElse(0);
        this.nextAgentId = maxId + 1;
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
        for (Agent agent : agents) {
            agent.getStrategy().setMaxDepth(newMaxDepth);
            agent.getStrategy().regenerateStrategy(newMaxDepth);  // Ensure the agent's strategy adheres to the new depth
        }
    }

    public void setSoloPhase(boolean isSoloPhase) {
        this.isSoloPhase = isSoloPhase;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    // Combined evaluation for both solo and competitive phases, based on isSoloPhase flag
    public void evaluateFitness(Population opponentPopulation) {
        if (isSoloPhase) {
            evaluateFitnessSolo();  // Evaluates for the entire population
        } else {
            evaluateFitnessCompetitive(opponentPopulation);  // Evaluates for the entire population
        }
    }
    
    public void evolve(Population opponentPopulation) {
        List<Agent> newAgents = new ArrayList<>();
        int eliteCount = (int) (elitismRate * agents.size());
    
        // Sort agents by fitness in descending order
        agents.sort(Comparator.comparingDouble(Agent::getFitness).reversed());
    
        // Preserve elite agents
        System.out.println("Preserving top " + eliteCount + " elite agents.");
        for (int i = 0; i < eliteCount; i++) {
            Agent eliteAgent = agents.get(i).clone();
            eliteAgent.setElite(true); // Mark the cloned agent as elite
            newAgents.add(eliteAgent);
            System.out.println("Elite Agent #" + eliteAgent.getNumber() + " Fitness: " + eliteAgent.getFitness() + " preserved.");
        }
    
        // Generate new offspring
        while (newAgents.size() < agents.size()) {
            Agent parent1 = selectAgent();
            Agent parent2;
            do {
                parent2 = selectAgent();
            } while (parent1 == parent2);
    
            System.out.println("Selected Parents:");
            System.out.println("  Parent1: Agent #" + parent1.getNumber() + " Fitness: " + parent1.getFitness());
            System.out.println("  Parent2: Agent #" + parent2.getNumber() + " Fitness: " + parent2.getFitness());
    
            Strategy offspringStrategy = parent1.getStrategy().crossover(parent2.getStrategy(), maxDepth);
            offspringStrategy.mutate(maxDepth);
    
            Agent offspring = new Agent(offspringStrategy, getNextAgentId());
            offspring.setElite(false); // Explicitly mark new agents as not elite
    
            // Evaluate offspring fitness based on the current phase
            if (isSoloPhase) {
                evaluateFitnessSoloForAgent(offspring);
            } else {
                evaluateFitnessCompetitiveForAgent(offspring, opponentPopulation);
            }
    
            newAgents.add(offspring);
            System.out.println("New Offspring Agent #" + offspring.getNumber() + " created, Fitness: " + offspring.getFitness());
        }
    
        agents = newAgents; // Replace old population with new one after all offspring are created
    
        // Debugging: Print fitness statistics after evolution
        System.out.println("After evolution:");
        System.out.println("  Best Fitness: " + getBestFitness());
        System.out.println("  Mean Fitness: " + getMeanFitness());
        System.out.println("  Fitness Variance: " + getFitnessVariance());
        System.out.println("  Fitness Standard Deviation: " + getFitnessStandardDeviation());
    }
    
    
    public void evaluateFitnessSolo() {
        for (Agent a : agents) {
            // Skip re-evaluation if the agent is elite (no changes in strategy)
            if (!a.isElite()) {
                evaluateFitnessSoloForAgent(a);
            }
            analyzePopulationDynamics();
        }
    }
    public void evaluateFitnessCompetitive(Population opponentPopulation) {
        for (Agent a : agents) {
            evaluateFitnessCompetitiveForAgent(a, opponentPopulation);
        }
    
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
    
        for (int i = 0; i < GPParameters.GAMES_TO_PLAY; i++) {
            Agent opponent = opponentPopulation.selectAgent();
            GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE, false);
    
            int result = simulateCompetition(agent, opponent, gameState);
    
            if (result == 1) wins++;
            else if (result == 0) draws++;
            else if (result == -1) losses++;
    
            totalTrailLength += gameState.calculateTrailLength(agent);
        }
    
        double winRate = (double) wins / GPParameters.GAMES_TO_PLAY;
        double drawRate = (double) draws / GPParameters.GAMES_TO_PLAY;
        double lossRate = (double) losses / GPParameters.GAMES_TO_PLAY;
    
        fitness += (winRate * GPParameters.WIN_WEIGHT);
        fitness += (drawRate * GPParameters.DRAW_WEIGHT);
        fitness -= (lossRate * GPParameters.LOSS_PENALTY * 0.5); // Reduce loss penalty
        fitness += (totalTrailLength / GPParameters.GAMES_TO_PLAY) * GPParameters.TRAIL_WEIGHT;
    
        // Ensure fitness doesn't go negative
        fitness = Math.max(fitness, 0.0);
        agent.setFitness(fitness);
    
        // Debugging outputs
        System.out.println("Agent #" + agent.getNumber() + " Competitive Fitness Calculated: " + fitness);
        System.out.println("  Wins: " + wins);
        System.out.println("  Draws: " + draws);
        System.out.println("  Losses: " + losses);
        System.out.println("  Average Trail Length: " + (totalTrailLength / GPParameters.GAMES_TO_PLAY));
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
          //  System.out.println("Agent #" + agent.getNumber() + " makes move " + move + " at step " + steps);
            gameStateSolo.update(move, -1);  // Ensure this method's logic is correct
            steps++;
    
            if (gameStateSolo.hasCollisionOccurred()) {
                hitWall = true;
          //      System.out.println("Collision Detected for Agent #" + agent.getNumber() + " at step " + steps);
                break;
            }
    
            // Assuming unique cell visits increase fitness
            int agentX = (int) gameStateSolo.getAgent1X();
            int agentY = (int) gameStateSolo.getAgent1Y();
            Point currentPosition = new Point(agentX, agentY);
            if (uniqueCellsVisited.add(currentPosition)) {
                fitness += GPParameters.EXPLORATION_WEIGHT;
          //      System.out.println("Agent #" + agent.getNumber() + " explores new cell at (" + agentX + ", " + agentY + "), Incremental Fitness: " + GPParameters.EXPLORATION_WEIGHT);
            } else {
                fitness -= GPParameters.REVISIT_PENALTY_WEIGHT;
          //      System.out.println("Agent #" + agent.getNumber() + " revisits cell at (" + agentX + ", " + agentY + "), Penalty Applied: " + GPParameters.REVISIT_PENALTY_WEIGHT);
            }
        }
    
        // Collision penalties
        if (hitWall) {
        
            fitness -= GPParameters.LOSS_PENALTY * (GPParameters.SOLO_MAX_STEPS - steps);
      //      System.out.println("Wall Hit Penalty Applied for Agent #" + agent.getNumber() + ", Penalty: " + (GPParameters.LOSS_PENALTY * (GPParameters.SOLO_MAX_STEPS - steps)));
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
        Agent redAgent = agent1.cloneWithNewNumber(1);
        Agent blueAgent = agent2.cloneWithNewNumber(2);
    
        while (!gameState.isGameOver()) {
            int move1 = redAgent.makeMove(gameState, redAgent.getNumber());
            int move2 = blueAgent.makeMove(gameState, blueAgent.getNumber());
    
            // Log the moves
            System.out.println("Agent #" + redAgent.getNumber() + " Move: " + move1);
            System.out.println("Agent #" + blueAgent.getNumber() + " Move: " + move2);
    
            gameState.update(move1, move2);
        }
    
        if (gameState.didAgentWin(redAgent)) {
            System.out.println("Agent #" + redAgent.getNumber() + " wins the game.");
            return 1;
        } else if (gameState.didAgentWin(blueAgent)) {
            System.out.println("Agent #" + blueAgent.getNumber() + " wins the game.");
            return -1;
        } else {
            System.out.println("Game ended in a draw.");
            return 0;
        }
                                        
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

    private double calculateDynamicRate(double initialRate, double finalRate, int currentGeneration, int totalGenerations) {
        return initialRate + (finalRate - initialRate) * ((double) currentGeneration / totalGenerations);
    }



    public static int getNextAgentId() {
        return nextAgentId++;
    }
    
}
