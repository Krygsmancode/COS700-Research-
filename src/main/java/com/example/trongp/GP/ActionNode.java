package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class ActionNode extends Node {
    private int action; // 0: Up, 1: Down, 2: Left, 3: Right

    public ActionNode(Random random) {
        super(1, random);
        this.action = random.nextInt(4); // Random action between 0 and 3
    }

    public ActionNode(int action, Random random) {
        super(1, random);
        this.action = action;
    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        return action;
    }

    @Override
    public Node clone() {
        return new ActionNode(this.action, random);
    }

    @Override
    public void mutate() {
        if (random.nextDouble() < GPParameters.MUTATION_RATE) {
            action = random.nextInt(4);
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
        int newAction = random.nextBoolean() ? this.action : otherNode.action;
        return new ActionNode(newAction, random);
    }
}
