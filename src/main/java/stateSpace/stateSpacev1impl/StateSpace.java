package stateSpace.stateSpacev1impl;

import interfaces.stateSpace.StateSpaceInterface;

/**
 * 
 * Implements state space interface and provides a container for
 * 
 * state space storage. It is a set of states and provides union
 * and intersetion operations. This implementation
 * implements the state space using array of doubles where each
 * row is a state
 * 
 * StateSpace
 */

public class StateSpace implements StateSpaceInterface {

    public double[][] states;
    public int dimension;
    public boolean[] isAlive; // A variable to store the status of each state
    private int maxStateCount; // A variable to pre-allocate memory for the expected number of states
    public int numberOfStates; // A variable to store the number of states which are active

    public StateSpace(int dimension, int maxStateCount) {
        this.dimension = dimension;
        this.states = new double[dimension][maxStateCount];
        this.isAlive = new boolean[maxStateCount];
        this.maxStateCount = maxStateCount;
        this.numberOfStates = 0;
    }

    /**
     * 
     * @param state : The state to be added to the state space
     * @return : True if the state was added successfully, false otherwise
     */
    public boolean addState(double[] state) {
        if (numberOfStates >= maxStateCount) {
            return false;
        }
        // Check for duplicates
        if (contains(state)) {
            return true; 
        }
        for (int i = 0; i < dimension; i++) {
            states[i][numberOfStates] = state[i];
        }
        isAlive[numberOfStates] = true;
        numberOfStates++;
        return true;
    }

    /**
     * DOD friendly method to copy a state directly from another StateSpace without allocating a double[]
     */
    private boolean addStateFrom(StateSpace other, int otherIndex) {
        if (numberOfStates >= maxStateCount) return false;
        if (!other.isAlive[otherIndex]) return false;
        if (containsFrom(other, otherIndex)) return true;

        for (int d = 0; d < dimension; d++) {
            states[d][numberOfStates] = other.states[d][otherIndex];
        }
        isAlive[numberOfStates] = true;
        numberOfStates++;
        return true;
    }

    public void inPlaceUnion(StateSpaceInterface stateSpaceInterface) {
        StateSpace stateSpace = (StateSpace) stateSpaceInterface;
        for (int i = 0; i < stateSpace.numberOfStates; i++) {
            if (stateSpace.isAlive[i]) {
                addStateFrom(stateSpace, i);
            }
        }
    }

    public StateSpaceInterface union(StateSpaceInterface stateSpaceInterface) {
        StateSpace stateSpace = (StateSpace) stateSpaceInterface;
        StateSpace result = new StateSpace(dimension, maxStateCount + stateSpace.maxStateCount);
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i]) result.addStateFrom(this, i);
        }
        for (int i = 0; i < stateSpace.numberOfStates; i++) {
            if (stateSpace.isAlive[i]) result.addStateFrom(stateSpace, i);
        }
        return result;
    }

    public void inPlaceIntersection(StateSpaceInterface stateSpaceInterface) {
        StateSpace stateSpace = (StateSpace) stateSpaceInterface;
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i]) {
                if (!stateSpace.containsFrom(this, i)) {
                    this.isAlive[i] = false;
                }
            }
        }
    }

    public StateSpaceInterface intersection(StateSpaceInterface stateSpaceInterface) {
        StateSpace stateSpace = (StateSpace) stateSpaceInterface;
        StateSpace result = new StateSpace(dimension, Math.min(maxStateCount, stateSpace.maxStateCount));
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i] && stateSpace.containsFrom(this, i)) {
                result.addStateFrom(this, i);
            }
        }
        return result;
    }

    public boolean contains(double[] state) {
        for (int i = 0; i < this.numberOfStates; i++) {
            if (!this.isAlive[i]) continue;
            boolean match = true;
            for (int d = 0; d < dimension; d++) {
                if (this.states[d][i] != state[d]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }

    public boolean containsFrom(StateSpace other, int otherIndex) {
        for (int i = 0; i < this.numberOfStates; i++) {
            if (!this.isAlive[i]) continue;
            boolean match = true;
            for (int d = 0; d < dimension; d++) {
                if (this.states[d][i] != other.states[d][otherIndex]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }

    public void inPlaceUnion(double[][] batchStates) {
        int batchSize = batchStates[0].length;
        for (int i = 0; i < batchSize; i++) {
            addStateFromBatch(batchStates, i);
        }
    }

    public StateSpaceInterface union(double[][] batchStates) {
        int batchSize = batchStates[0].length;
        StateSpace result = new StateSpace(dimension, maxStateCount + batchSize);
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i]) result.addStateFrom(this, i);
        }
        for (int i = 0; i < batchSize; i++) {
            result.addStateFromBatch(batchStates, i);
        }
        return result;
    }

    public void inPlaceIntersection(double[][] batchStates) {
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i]) {
                if (!batchContains(batchStates, this, i)) {
                    this.isAlive[i] = false;
                }
            }
        }
    }

    public StateSpaceInterface intersection(double[][] batchStates) {
        int batchSize = batchStates[0].length;
        StateSpace result = new StateSpace(dimension, Math.min(maxStateCount, batchSize));
        for (int i = 0; i < this.numberOfStates; i++) {
            if (this.isAlive[i] && batchContains(batchStates, this, i)) {
                result.addStateFrom(this, i);
            }
        }
        return result;
    }

    private boolean addStateFromBatch(double[][] batchStates, int index) {
        if (numberOfStates >= maxStateCount) return false;
        if (containsFromBatch(batchStates, index)) return true;

        for (int d = 0; d < dimension; d++) {
            states[d][numberOfStates] = batchStates[d][index];
        }
        isAlive[numberOfStates] = true;
        numberOfStates++;
        return true;
    }

    private boolean containsFromBatch(double[][] batchStates, int index) {
        for (int i = 0; i < this.numberOfStates; i++) {
            if (!this.isAlive[i]) continue;
            boolean match = true;
            for (int d = 0; d < dimension; d++) {
                if (this.states[d][i] != batchStates[d][index]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }

    private boolean batchContains(double[][] batchStates, StateSpace source, int sourceIndex) {
        int batchSize = batchStates[0].length;
        for (int i = 0; i < batchSize; i++) {
            boolean match = true;
            for (int d = 0; d < dimension; d++) {
                if (batchStates[d][i] != source.states[d][sourceIndex]) {
                    match = false;
                    break;
                }
            }
            if (match) return true;
        }
        return false;
    }

}
