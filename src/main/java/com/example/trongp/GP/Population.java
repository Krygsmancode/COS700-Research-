package com.example.trongp.GP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            for (Agent opponent : opponentPopulation.getAgents()) {
                if (agent != opponent) {
                    boolean result = agent.compete(opponent);
                    wins += result ? 1 : 0;
                }
            }
            double winRatio = (double) wins / (GPParameters.GAMES_TO_PLAY * opponentPopulation.getAgents().size());
            double calculatedFitness = winRatio * GPParameters.WIN_WEIGHT;
            agent.setFitness(calculatedFitness);
            System.out.println("Agent " + agent.getNumber() + " fitness: " + agent.getFitness());
        });
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
        return new Agent(offspringStrategy, parent1.getNumber());
    }

    private void mutate(Agent agent) {
        agent.getStrategy().mutate();
    }

    public double calculateAverageFitness() {
        return agents.stream().mapToDouble(Agent::getFitness).average().orElse(0.0);
    }

    public double calculateFitnessVariance() {
        double averageFitness = calculateAverageFitness();
        return agents.stream().mapToDouble(agent -> Math.pow(agent.getFitness() - averageFitness, 2)).sum() / agents.size();
    }
}
