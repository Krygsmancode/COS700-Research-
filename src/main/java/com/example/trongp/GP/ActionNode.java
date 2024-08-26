package com.example.trongp.GP;

import com.example.trongp.GameState;

public class ActionNode extends Node {
    private int action; // 0: up, 1: down, 2: left, 3: right

    public ActionNode() {
        super(1);
        this.action = random.nextInt(4);
    }

    public ActionNode(int action) {
        super(1);
        this.action = action;
    }

    @Override
    public int evaluate(GameState gameState, int agentNumber) {
        return action;
    }

    @Override
    public Node crossover(Node other) {
        return random.nextBoolean() ? this : other;
    }

    @Override
    public void mutate() {
        action = random.nextInt(4);
    }

    @Override
    public String toString() {
        return "ActionNode(Action: " + action + ")";
    }
}
