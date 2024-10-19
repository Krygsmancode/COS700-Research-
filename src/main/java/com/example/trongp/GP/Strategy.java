package com.example.trongp.GP;

import com.example.trongp.GameState;
import java.util.Random;

public class Strategy implements Cloneable {
    private Node root;
    private Random random;



    

    public Strategy(Node root, Random random) {
        this.root = root;
        this.random = random;
    }

    public Strategy crossover(Strategy other, int maxDepth, double crossoverRate) {
        Random rand = new Random();
        if (rand.nextDouble() < crossoverRate) {
            // Perform crossover logic
        }
        // If crossover does not happen, return a clone of one parent
        return this.clone();
    }
    
    public Strategy(int maxDepth, Random random, boolean useFullMethod) {
        this.random = random;
        if (useFullMethod) {
            this.root = NodeFactory.full(maxDepth, random);
        } else {
            this.root = NodeFactory.grow(maxDepth, random);
        }
        if (this.getMaxDepth() > maxDepth) {
            System.err.println("Error: Created strategy exceeds max depth!");
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
            return new Strategy(this.root.clone(), this.random);
        }
        Strategy offspring = new Strategy(newRoot, this.random);
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

    public void mutate(int maxDepth) {
        root.mutate();
        if (getMaxDepth() > maxDepth) {
            prune(maxDepth);
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


    public void regenerateStrategy(int newMaxDepth) {
        this.root = NodeFactory.createRandomNode(newMaxDepth, random);
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
    return new Strategy(this.root.clone(), this.random);
}
  
public Node getRootNode() {
    return root; // Return the root of the decision tree
}


}
