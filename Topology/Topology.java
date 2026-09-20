package Topology;

import interfaces.topology.TopologyInterface;

public class Topology implements TopologyInterface {

    private double[] parameter;

    public Topology() {
        this.parameter = null;
        this.build(1);
    }

    public Topology(double[] parameters) {
        this.parameter = parameters;
        this.build(1);
    }

    public Topology(int steps) {
        this.parameter = null;
        this.build(steps);
    }

    public Topology(double[] parameter, int steps) {
        this.parameter = parameter;
        this.build(steps);
    }

    @Override
    public void setParameters(double[] parameters) {
        this.parameter = parameters;
    }

    @Override
    public double[][] getNeighbourStates(double[] state) {
        throw new UnsupportedOperationException("Unimplemented method 'getNeighbourStates'");
    }

    @Override
    public void getNeighbourStates(double[] state, double[][] outBuffer) {
        throw new UnsupportedOperationException("Unimplemented method 'getNeighbourStates'");
    }

    @Override
    public void build(int steps) {
        System.out.print("Needs to be defined\n");
    }

    @Override
    public void getBatchNeighbours(double[][] states, double[][][] stateBuffer) {
        int dim = states.length;
        if (dim == 0) return;
        int numElements = states[0].length;
        if (numElements == 0) return;
        
        int numNeighbors = stateBuffer[0][0].length;

        // Allocate temporary buffers once per batch call
        double[] singleState = new double[dim];
        double[][] singleOutBuffer = new double[dim][numNeighbors];

        for (int i = 0; i < numElements; i++) {
            // 1. Extract single state from DOD arrays
            for (int d = 0; d < dim; d++) {
                singleState[d] = states[d][i];
            }
            
            // 2. Compute neighbours
            getNeighbourStates(singleState, singleOutBuffer);
            
            // 3. Insert into massive DOD batch buffer
            for (int d = 0; d < dim; d++) {
                for (int n = 0; n < numNeighbors; n++) {
                    stateBuffer[d][i][n] = singleOutBuffer[d][n];
                }
            }
        }
    }

}
