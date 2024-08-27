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
        if (Math.random() < 0.5) {
            return new DecisionNode(maxDepth); // Non-terminal decision node
        } else {
            return new ActionNode(); // Terminal action node
        }
    }

    public static Strategy createRandomNode(int maxDepth, boolean b) {
        return new Strategy(createRandomNode(maxDepth));
    }
}
