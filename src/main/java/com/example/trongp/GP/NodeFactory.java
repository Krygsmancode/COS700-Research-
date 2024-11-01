package com.example.trongp.GP;

import java.util.Random;

public class NodeFactory {

    public static Node grow(int maxDepth, Random random, boolean isPhase2) {
        if (maxDepth <= 0) {
            // Return a terminal node
            return new ActionNode(random);
        } else {
            boolean chooseTerminal = random.nextBoolean();
            if (chooseTerminal) {
                return new ActionNode(random);
            } else {
                int decisionFeature = selectFeatureIndex(random, isPhase2);
                double threshold = random.nextDouble();
                Node leftChild = grow(maxDepth - 1, random, isPhase2);
                Node rightChild = grow(maxDepth - 1, random, isPhase2);
                return new DecisionNode(decisionFeature, threshold, leftChild, rightChild, random);
            }
        }
    }
    
    public static Node full(int maxDepth, Random random, boolean isPhase2) {
        if (maxDepth <= 0) {
            // Return a terminal node
            return new ActionNode(random);
        } else {
            int decisionFeature = selectFeatureIndex(random, isPhase2);
            double threshold = random.nextDouble();
            Node leftChild = full(maxDepth - 1, random, isPhase2);
            Node rightChild = full(maxDepth - 1, random, isPhase2);
            return new DecisionNode(decisionFeature, threshold, leftChild, rightChild, random);
        }
    }
    
    static int selectFeatureIndex(Random random, boolean isPhase2) {
        if (isPhase2) {
            // In Phase 2, increase the probability of selecting opponent features
            double randValue = random.nextDouble();
            if (randValue < 0.5) {
                // 50% chance to select opponent-related features (indices 11 and 12)
                return 11 + random.nextInt(2);
            } else {
                // 50% chance to select other features (indices 0 to 10)
                return random.nextInt(11);
            }
        } else {
            // In Phase 1, select from features 0 to 10
            return random.nextInt(11);
        }
    }
    
    public static Node createRandomNode(int maxDepth, Random random, boolean isPhase2) {
        if (random.nextBoolean()) {
            return grow(maxDepth, random, isPhase2);
        } else {
            return full(maxDepth, random, isPhase2);
        }
    }
    
}

