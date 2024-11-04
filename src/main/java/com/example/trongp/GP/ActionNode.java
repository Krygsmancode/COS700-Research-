package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class ActionNode extends Node {
    private int action; // 0: Up, 1: Down, 2: Left, 3: Right
    private Random random;

    public ActionNode(Random random) {
        super(1, random);
        this.random = random;
        this.action = random.nextInt(4); // Random action between 0 and 3
    }

    public ActionNode(int action, Random random) {
        super(1, random);
        this.action = action;
        this.random = random;  // Make sure this is always initialized

    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        return action;
    }

    @Override
    public Node clone() {
        return new ActionNode(this.action, this.random);  
    }
    


    @Override
    public void mutate(boolean isPhase2) {
         double mutationRate = isPhase2 ? GPParameters.PHASE2_MUTATION_RATE : GPParameters.MUTATION_RATE;
         if (random.nextDouble() < mutationRate) {
             this.action = random.nextInt(4);
         }
    }
    


    @Override
    public String toString() {
        String actionStr;
        switch (action) {
            case 0: actionStr = "Up"; break;
            case 1: actionStr = "Down"; break;
            case 2: actionStr = "Left"; break;
            case 3: actionStr = "Right"; break;
            default: actionStr = "UnknownAction"; break;
        }
        return "Action(" + actionStr + ")";
    }

    @Override
    public Node crossover(Node other) {
        if (!(other instanceof ActionNode)) {
            System.err.println("Crossover attempted between incompatible types. Returning a clone.");
            return this.clone();
        }
        ActionNode otherNode = (ActionNode) other;
        if (this.random == null || otherNode.random == null) {
            throw new IllegalStateException("Random instance is null during crossover.");
        }
        int newAction = this.random.nextBoolean() ? this.action : otherNode.action;
        return new ActionNode(newAction, this.random);  // Use the same Random instance
    }
}
