package com.example.trongp.GP;

import java.util.Random;

public class NodeFactory {
    public static Node grow(int depth, Random random) {
        if (depth <= 2) {
            return new ActionNode(random);
        } else {
            int decisionFeature = random.nextInt(DecisionNode.NUM_FEATURES); // Adjusted if you have a specific number of features
            double threshold = random.nextDouble(); // Generate a random threshold
            Node leftChild = grow(depth - 1, random);
            Node rightChild = grow(depth - 1, random);
            return new DecisionNode(decisionFeature, threshold, leftChild, rightChild, random);
        }
    }

    public static Node full(int depth, Random random) {
        if (depth <= 2) {
            return new ActionNode(random);
        } else {
            int decisionFeature = random.nextInt(DecisionNode.NUM_FEATURES);
            double threshold = random.nextDouble(); // Generate a random threshold for each decision node
            Node leftChild = full(depth - 1, random);
            Node rightChild = full(depth - 1, random);
            return new DecisionNode(decisionFeature, threshold, leftChild, rightChild, random);
        }
    }


    public static Node createRandomNode(int depth, Random random) {
        return random.nextBoolean() ? grow(depth, random) : full(depth, random);
    }
}
