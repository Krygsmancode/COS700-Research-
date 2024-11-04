package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class Strategy implements Cloneable {
    private Node root;
    private Random random;
    private boolean isPhase2; 


    

    public Strategy(Node root, Random random, boolean isPhase2) {
        this.root = root;
        this.random = random;
        this.isPhase2 = isPhase2;

    }

    public Strategy crossover(Strategy other, int maxDepth, double crossoverRate) {
        Random rand = this.random;
        if (rand.nextDouble() < crossoverRate) {
            return this.crossover(other, maxDepth);
        }
        // If crossover does not happen, return a clone of one parent
        return this.clone();
    }

// In Strategy.java
public Strategy(int maxDepth, Random random, boolean useFullMethod, boolean isPhase2) {
    this.random = random;
    this.isPhase2 = isPhase2;
    if (useFullMethod) {
        this.root = NodeFactory.full(maxDepth, random, isPhase2);
    } else {
        this.root = NodeFactory.grow(maxDepth, random, isPhase2);
    }
}

    

    public int calculateTreeDistance(Strategy other) {
        return calculateNodeDistance(this.root, other.getRoot());
    }

    private int calculateNodeDistance(Node node1, Node node2) {
        if (node1 == null && node2 == null) {
            return 0; // Both are null, so they are the same
        } else if (node1 == null || node2 == null) {
            return 1; // One is null and the other isn't, so they are different
        }
    
        // If nodes are of different types, increase the distance
        int distance = (node1.getClass() != node2.getClass()) ? 1 : 0;
    
        if (node1 instanceof DecisionNode && node2 instanceof DecisionNode) {
            DecisionNode decision1 = (DecisionNode) node1;
            DecisionNode decision2 = (DecisionNode) node2;
    
            // Compare the decision features and thresholds
            if (decision1.decisionFeature != decision2.decisionFeature) {
                distance++;
            }
            if (decision1.getThreshold() != decision2.getThreshold()) {
                distance++;
            }
    
            // Recursively compare left and right subtrees
            distance += calculateNodeDistance(decision1.left, decision2.left);
            distance += calculateNodeDistance(decision1.right, decision2.right);
        } else if (node1 instanceof ActionNode && node2 instanceof ActionNode) {
            // If both nodes are action nodes, they are the same
            // (For simplicity, assuming all action nodes are equivalent)
        } else {
            // If they are different types of nodes, treat them as different
            distance++;
        }
    
        return distance;
    }


    public Strategy(Node root) {
        this.root = root;
    }

    public int execute(GameState gameState, int agentNumber) {
        return root.evaluate(gameState, agentNumber);
    }

    public String getTreeRepresentation() {
        return root.toString();
    }


    public Strategy crossover(Strategy other, int maxDepth) {
        Node newRoot = crossoverNodes(this.root, other.getRoot());
        if (newRoot == null) {
            return this.clone();
        }
        Strategy offspring = new Strategy(newRoot, this.random, this.isPhase2); // Pass isPhase2
        if (offspring.getMaxDepth() > maxDepth) {
            offspring.prune(maxDepth);
        }
        return offspring;
    }
    

    public void prune(int maxDepth) {
        this.root = pruneNode(this.root, maxDepth);
    }
    
    private Node pruneNode(Node node, int maxDepth) {
        if (maxDepth <= 1 || node instanceof ActionNode) {
            return new ActionNode(random);
        }
        if (node instanceof DecisionNode) {
            DecisionNode decisionNode = (DecisionNode) node;
            Node left = pruneNode(decisionNode.left, maxDepth - 1);
            Node right = pruneNode(decisionNode.right, maxDepth - 1);
            double threshold = decisionNode.getThreshold(); // Assuming getThreshold() method exists
            return new DecisionNode(decisionNode.decisionFeature, threshold, left, right, random);

            
        }
        return node; // Should not reach here
    }

    public void setMaxDepth(int newMaxDepth) {
        if (getMaxDepth() > newMaxDepth) {
            prune(newMaxDepth);
        }
    }
    
    
    

    private Node crossoverNodes(Node one, Node two) {
        if (one == null || two == null) {
            System.err.println("Attempted to crossover with null node.");
            return nonNullParent(one, two);
        }
        if (!one.isCompatibleForCrossover(two)) {
            System.err.println("Incompatible nodes, selecting a non-null parent node.");
            return nonNullParent(one, two);
        }
        Node result = one.crossover(two);
        if (result == null) {
            System.err.println("Crossover resulted in null, selecting a non-null parent node.");
            return nonNullParent(one, two);
        }
        return result;
    }

    private Node nonNullParent(Node one, Node two) {
        return one != null ? one : two;
    }

// In Strategy.java
public void mutate(int maxDepth) {

    root.mutate(isPhase2);
    if (getMaxDepth() > maxDepth) {
        prune(maxDepth);
    } else {
        // Allow tree to grow if depth is less than maxDepth
        expandTree(root, maxDepth - getMaxDepth(), isPhase2);
    }
}


    public int getMaxDepth() {
        return root.getMaxDepth();
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void regenerateStrategy(int newMaxDepth, boolean isPhase2) {
        this.isPhase2 = isPhase2;  // Update the phase state
        this.root = NodeFactory.createRandomNode(newMaxDepth, random, isPhase2);
    }
    
    

    // @Override
    // public Strategy clone() {
    //     try {
    //         Strategy cloned = (Strategy) super.clone();
    //         cloned.random = this.random;
    //         cloned.root = this.root.clone();
    //         return cloned;
    //     } catch (CloneNotSupportedException e) {
    //         throw new RuntimeException(e);
    //     }
    // }

    @Override
    public Strategy clone() {
        Strategy clonedStrategy = new Strategy(this.root.clone(), this.random, this.isPhase2);
        return clonedStrategy;
    }
    
    
    
    
  
public Node getRootNode() {
    return root; // Return the root of the decision tree
}

public void adjustTreeDepth(int newMaxDepth) {
    if (getMaxDepth() < newMaxDepth) {
        expandTree(root, newMaxDepth - getMaxDepth(), isPhase2);
    }
}


private void expandTree(Node node, int depthToAdd, boolean isPhase2) {
    if (depthToAdd <= 0) {
        return;
    }
    if (node instanceof ActionNode) {
        // Replace ActionNode with DecisionNode
        Node leftChild = new ActionNode(random);
        Node rightChild = new ActionNode(random);
        int decisionFeature = NodeFactory.selectFeatureIndex(random, isPhase2); // Call static method from NodeFactory
        double threshold = random.nextDouble();
        DecisionNode newNode = new DecisionNode(decisionFeature, threshold, leftChild, rightChild, random);
        // Replace node
        if (node == root) {
            root = newNode;
        } else {
            // Find and replace in the tree
            replaceNode(root, node, newNode);
        }
        // Recursively expand
        expandTree(newNode.left, depthToAdd - 1, isPhase2);
        expandTree(newNode.right, depthToAdd - 1, isPhase2);
    } else if (node instanceof DecisionNode) {
        DecisionNode decisionNode = (DecisionNode) node;
        expandTree(decisionNode.left, depthToAdd - 1, isPhase2);
        expandTree(decisionNode.right, depthToAdd - 1, isPhase2);
    }
}







private void replaceNode(Node parent, Node oldNode, Node newNode) {
    if (parent instanceof DecisionNode) {
        DecisionNode decisionNode = (DecisionNode) parent;
        if (decisionNode.left == oldNode) {
            decisionNode.left = newNode;
        } else if (decisionNode.right == oldNode) {
            decisionNode.right = newNode;
        } else {
            replaceNode(decisionNode.left, oldNode, newNode);
            replaceNode(decisionNode.right, oldNode, newNode);
        }
    }
}

public void setPhase(boolean b) {
    isPhase2 = b;
}



}
