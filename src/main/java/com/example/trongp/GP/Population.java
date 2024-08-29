package com.example.trongp.GP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.example.trongp.GameState;

public class Population {
    private List<Agent> agents;
    private Random random ;

    public Population(int size, int maxDepth, Random random) {
        this.random = random;
        agents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            // Generate a random strategy tree for each agent
            Strategy randomStrategy = new Strategy(maxDepth);
            agents.add(new Agent(randomStrategy, i + 1));
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public void evolve() {
        List<Agent> newAgents = new ArrayList<>();
        while (newAgents.size() < agents.size()) {
            Agent parent1 = selectAgent();
            Agent parent2 = selectAgent();
            Agent offspring = crossover(parent1, parent2);
            mutate(offspring);
            newAgents.add(offspring);
        }
        agents = newAgents;
    }
    public void evaluateFitness(Population opponentPopulation) {
        agents.forEach(agent -> {
            int wins = 0;
            double totalWallDistance = 0;
            double totalTrailLength = 0;
            double collisionPenalty = 0;
    
            double maxPossibleWallDistance = 0; // This needs to be calculated based on your game dynamics
            double maxPossibleTrailLength = GPParameters.GRID_SIZE * GPParameters.GRID_SIZE; // Example calculation
    
            for (Agent opponent : opponentPopulation.getAgents()) {
                if (agent != opponent) {
                    GameState gameState = new GameState(GPParameters.GRID_SIZE, GPParameters.GRID_SIZE);
                    int steps = 0;
    
                    while (!gameState.isGameOver()) {
                        int agentMove = agent.makeMove(gameState, agent.getNumber());
                        int opponentMove = opponent.makeMove(gameState, opponent.getNumber());
                        gameState.update(agentMove, opponentMove);
    
                        double agentX = agent.getNumber() == 1 ? gameState.getAgent1X() : gameState.getAgent2X();
                        double agentY = agent.getNumber() == 1 ? gameState.getAgent1Y() : gameState.getAgent2Y();
                        double distance = Math.min(
                            Math.min(distanceToWall(gameState, agentX, agentY, "UP"), distanceToWall(gameState, agentX, agentY, "DOWN")),
                            Math.min(distanceToWall(gameState, agentX, agentY, "LEFT"), distanceToWall(gameState, agentX, agentY, "RIGHT"))
                        );
                        totalWallDistance += distance;
                        maxPossibleWallDistance += Math.max(distance, maxPossibleWallDistance);
                        steps++;
                    }
    
                    if (gameState.isGameOver() && !gameState.didAgentWin(agent)) {
                        collisionPenalty += calculateCollisionPenalty(steps, GPParameters.GRID_SIZE);
                    }
    
                    if (gameState.didAgentWin(agent)) {
                        wins++;
                    }
    
                    totalTrailLength += gameState.calculateTrailLength(agent);
                }
            }
    
            double winRatio = (double) wins / (GPParameters.GAMES_TO_PLAY * opponentPopulation.getAgents().size());
            double normalizedWallDistance = totalWallDistance / maxPossibleWallDistance;
            double normalizedTrailLength = totalTrailLength / maxPossibleTrailLength;
    
            double calculatedFitness = winRatio * GPParameters.WIN_WEIGHT + normalizedWallDistance * GPParameters.TRAIL_WEIGHT - collisionPenalty;
            agent.setFitness(calculatedFitness);
        });
    }
    
    private double calculateCollisionPenalty(int steps, int gridSize) {
        if (steps < (gridSize * gridSize) / 2) {
            return 50.0 * (1 + (0.5 - ((double)steps / (gridSize * gridSize))));
        } else {
            return 25.0;
        }
    }
    
    

    

private double distanceToWall(GameState gameState, double agentX, double agentY, String direction) {
    if (direction.equals("UP")) {
        return agentY;
    } else if (direction.equals("DOWN")) {
        return gameState.getHeight() - agentY - 1;  // Subtract 1 because coordinates are zero-indexed
    } else if (direction.equals("LEFT")) {
        return agentX;
    } else {  // "RIGHT"
        return gameState.getWidth() - agentX - 1;  // Subtract 1 because coordinates are zero-indexed
    }
}


    public Agent getBestAgent() {
        return agents.stream().max((a, b) -> Double.compare(a.getFitness(), b.getFitness())).orElse(null);
    }

  

    private Agent selectAgent() {
        Agent best = null;
        for (int i = 0; i < GPParameters.TOURNAMENT_SIZE; i++) {
            Agent candidate = agents.get(random.nextInt(agents.size()));
            if (best == null || candidate.getFitness() > best.getFitness()) {
                best = candidate;
            }
        }
        return best;
    }

    private Agent crossover(Agent parent1, Agent parent2) {
        Strategy offspringStrategy = parent1.getStrategy().crossover(parent2.getStrategy());
        if (offspringStrategy.getMaxDepth() > GPParameters.MAX_DEPTH) {
            offspringStrategy = new Strategy(GPParameters.MAX_DEPTH); // Regenerate if depth is violated
        }
        return new Agent(offspringStrategy, parent1.getNumber());
    }
    
    private void mutate(Agent agent) {
        agent.getStrategy().mutate();
        if (agent.getStrategy().getMaxDepth() > GPParameters.MAX_DEPTH) {
            agent.setStrategy(new Strategy(GPParameters.MAX_DEPTH)); // Regenerate if depth is violated
        }
    }
    

    public double calculateAverageFitness() {
        return agents.stream().mapToDouble(Agent::getFitness).average().orElse(0.0);
    }

    public double calculateFitnessVariance() {
        double averageFitness = calculateAverageFitness();
        return agents.stream().mapToDouble(agent -> Math.pow(agent.getFitness() - averageFitness, 2)).sum() / agents.size();
    }
}
