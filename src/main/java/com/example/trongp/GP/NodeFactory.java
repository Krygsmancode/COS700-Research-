package com.example.trongp.GP;

public class NodeFactory {

    public static Node createRandomNode(int maxDepth, boolean isRoot) {
        if (maxDepth <= 0) {
            return new ActionNode(); // Terminal node with a random action
        }
        if (isRoot || Math.random() < 0.5) {
            return new DecisionNode(maxDepth); // Non-terminal decision node
        } else {
            return new ActionNode(); // Terminal action node
        }
    }
}
