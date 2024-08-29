package com.example.trongp.GP;

import java.util.Random;

public class NodeFactory {

    private static Random random;

    public static void setRandomSeed(Random rand) {
        random = rand;
    }
    public static Node createRandomNode(int maxDepth) {
        if (maxDepth <= 0) {
            return new ActionNode(); // Terminal node with a random action
        }
        if (random.nextDouble() < 0.5) { // Ensure randomness is consistent with seed
            int decisionFeature = random.nextInt(6); // Randomly select a decision feature
            return new DecisionNode(decisionFeature, createRandomNode(maxDepth - 1), createRandomNode(maxDepth - 1)); // Create a decision node with a valid decisionFeature
        } else {
            return new ActionNode(); // Terminal action node
        }
    }
    

    public static Strategy createRandomNode(int maxDepth, boolean b) {
        return new Strategy(createRandomNode(maxDepth));
    }
}
