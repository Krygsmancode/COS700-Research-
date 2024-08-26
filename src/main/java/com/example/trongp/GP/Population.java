package com.example.trongp.GP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.example.trongp.FitnessPlotter;

public class Population {
    private List<Agent> agents;
    private Random random = new Random();
    private int totalGames = GPParameters.GAMES_TO_PLAY;

    public Population(int size, int maxDepth) {
        agents = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            agents.add(new Agent(NodeFactory.createRandomNode(maxDepth, true), i + 1));
        }
    }

    

    public void evolve() {
        List<Agent> newAgents = new ArrayList<>();

        // Elitism: Preserve the best agent from the current generation
        Agent bestAgent = getBestAgent();
        newAgents.add(bestAgent.clone()); // Ensure the best agent is cloned and not directly referenced
        logEliteAgent(bestAgent); // Log the elite agent's fitness

        while (newAgents.size() < agents.size()) {
            Agent parent1 = selectAgent();
            Agent parent2 = selectAgent();
            Agent child = crossover(parent1, parent2);
            mutate(child);
            newAgents.add(child);
        }
        this.agents = newAgents;
    }

    private void logEliteAgent(Agent bestAgent) {
      System.out.println(bestAgent);
    }



    public void evaluateFitness(Population opponentPopulation) {
        agents.forEach(agent -> {
            int wins = 0;
            for (Agent opponent : opponentPopulation.agents) {
                if (agent != opponent) {
                    boolean result = agent.compete(opponent); // Assumes Agent has a compete method
                    wins += result ? 1 : 0;
                }
            }
            double winRatio = (double) wins / (GPParameters.GAMES_TO_PLAY * opponentPopulation.agents.size());
            agent.setFitness(winRatio * GPParameters.WIN_WEIGHT);
        });
    }
    public Agent getBestAgent() {
        return Collections.max(agents, Comparator.comparingDouble(Agent::getFitness));
    }
    
    

    private Agent selectAgent() {
        List<Agent> tournament = new ArrayList<>();
        for (int i = 0; i < GPParameters.TOURNAMENT_SIZE; i++) {
            Agent candidate = agents.get(random.nextInt(agents.size()));
            tournament.add(candidate);
        }
        return Collections.max(tournament, Comparator.comparingDouble(Agent::getFitness));
    }

    private Agent crossover(Agent parent1, Agent parent2) {
        return new Agent(parent1.getStrategy().crossover(parent2.getStrategy()), parent1.getNumber());
    }

    private void mutate(Agent agent) {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            agent.getStrategy().mutate();
        }
    }

    public double calculateAverageFitness() {
        return agents.stream().mapToDouble(Agent::getFitness).average().orElse(0.0);
    }

    public double calculateMaxFitness() {
        return agents.stream().mapToDouble(Agent::getFitness).max().orElse(0.0);
    }

    public double calculateFitnessVariance() {
        double average = calculateAverageFitness();
        double variance = agents.stream().mapToDouble(agent -> Math.pow(agent.getFitness() - average, 2)).sum();
        return variance / agents.size();
    }



   
}
