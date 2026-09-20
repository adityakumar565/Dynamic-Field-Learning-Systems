package Transition;

import interfaces.Transition.AgentTransitionInterface;

public class AgentTransition implements AgentTransitionInterface {

    private double[] parameters;

    public AgentTransition() {
        this.parameters = null;
    }

    public AgentTransition(double[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public void setParameters(double[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public double[] getParameters() {
        return this.parameters;
    }

    @Override
    public double[] getNextState(double[][] states, double[][] fields) {
        // Hollow Skeleton Phase 2: return the first neighbor as the next state
        int stateDim = states.length;
        double[] nextState = new double[stateDim];
        for (int d = 0; d < stateDim; d++) {
            nextState[d] = states[d][0];
        }
        return nextState;
    }

    @Override
    public void getNextState(double[][] state, double[][] fields, double[] nextState) {
        int stateDim = state.length;
        if (stateDim == 0) return;
        int numNeighbors = state[0].length;
        
        int bestNeighborIndex = 0;
        
        if (fields.length > 0 && fields[0].length > 0) {
            double maxField = -Double.MAX_VALUE;
            for (int n = 0; n < numNeighbors; n++) {
                double currentField = fields[0][n]; // Looking at primary field dimension 0
                if (currentField > maxField) {
                    maxField = currentField;
                    bestNeighborIndex = n;
                }
            }
        }

        // Select the state of the best neighbor
        for (int d = 0; d < stateDim; d++) {
            nextState[d] = state[d][bestNeighborIndex];
        }
    }

    @Override
    public void getBatchNextState(double[][][] states, double[][][] fields, double[][] nextStates) {
        int stateDim = states.length;
        if (stateDim == 0) return;
        int numElements = states[0].length;
        if (numElements == 0) return;
        int numNeighbors = states[0][0].length;

        int fieldDim = fields.length;

        // Allocate temporary buffers once per batch call
        double[][] singleStates = new double[stateDim][numNeighbors];
        double[][] singleFields = new double[fieldDim][numNeighbors];
        double[] singleNextState = new double[stateDim];

        for (int i = 0; i < numElements; i++) {
            // 1. Extract the neighborhood context for this single agent
            for (int n = 0; n < numNeighbors; n++) {
                for (int d = 0; d < stateDim; d++) {
                    singleStates[d][n] = states[d][i][n];
                }
                for (int f = 0; f < fieldDim; f++) {
                    if (fieldDim > 0 && fields[0].length > 0) {
                        singleFields[f][n] = fields[f][i][n];
                    }
                }
            }

            // 2. Compute next state via Policy
            getNextState(singleStates, singleFields, singleNextState);

            // 3. Insert into massive DOD batch buffer
            for (int d = 0; d < stateDim; d++) {
                nextStates[d][i] = singleNextState[d];
            }
        }
    }

}
