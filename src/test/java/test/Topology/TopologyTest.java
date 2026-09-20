package test.Topology;

import Topology.Topology;

public class TopologyTest {

    public static void main(String[] args) {

        class ManhattenTopology extends Topology {
            @Override
            public double[][] getNeighbourStates(double[] state) {
                double x = state[0];
                double y = state[1];
                return new double[][] { { x + 1, y }, { x - 1, y }, { x, y + 1 }, { x, y - 1 } };
            }

            @Override
            public void getNeighbourStates(double[] state, double[][] outBuffer) {
                double x = state[0];
                double y = state[1];
                // Dimension 0 (X)
                outBuffer[0][0] = x + 1;
                outBuffer[0][1] = x - 1;
                outBuffer[0][2] = x;
                outBuffer[0][3] = x;
                
                // Dimension 1 (Y)
                outBuffer[1][0] = y;
                outBuffer[1][1] = y;
                outBuffer[1][2] = y + 1;
                outBuffer[1][3] = y - 1;
            }

            @Override
            public void getBatchNeighbours(double[][] states, double[][][] stateBuffer) {
                int numElements = states[0].length;
                for (int i = 0; i < numElements; i++) {
                    double x = states[0][i];
                    double y = states[1][i];
                    
                    // Dimension 0 (X)
                    stateBuffer[0][i][0] = x + 1;
                    stateBuffer[0][i][1] = x - 1;
                    stateBuffer[0][i][2] = x;
                    stateBuffer[0][i][3] = x;
                    
                    // Dimension 1 (Y)
                    stateBuffer[1][i][0] = y;
                    stateBuffer[1][i][1] = y;
                    stateBuffer[1][i][2] = y + 1;
                    stateBuffer[1][i][3] = y - 1;
                }
            }

        }

        Topology topology = new ManhattenTopology();
        double[] state = { 0, 0 };
        System.out.println("--- Old Allocation Method ---");
        double[][] neighbourStates = topology.getNeighbourStates(state);
        for (double[] neighbourState : neighbourStates) {
            System.out.println("Neighbour State: " + neighbourState[0] + ", " + neighbourState[1]);
        }

        System.out.println("\n--- DOD Zero-Allocation Method ---");
        // DOD Layout: [dimension][numNeighbours]
        double[][] outBuffer = new double[2][4]; 
        topology.getNeighbourStates(state, outBuffer);
        
        for (int i = 0; i < 4; i++) {
            System.out.println("Neighbour State " + i + ": " + outBuffer[0][i] + ", " + outBuffer[1][i]);
        }

        System.out.println("\n--- DOD Batch Zero-Allocation Method ---");
        double[][] batchStates = new double[][] {
            { 0.0, 10.0 }, // Dimension 0 (X)
            { 0.0, 10.0 }  // Dimension 1 (Y)
        };
        // Expect 2 states * 4 neighbours = 8
        double[][][] batchBuffer = new double[2][2][4];
        topology.getBatchNeighbours(batchStates, batchBuffer);
        
        System.out.println("Batch State 1 (0, 0) Neighbours:");
        for(int i = 0; i < 4; i++) {
            System.out.println(batchBuffer[0][0][i] + ", " + batchBuffer[1][0][i]);
        }
        
        System.out.println("Batch State 2 (10, 10) Neighbours:");
        for(int i = 0; i < 4; i++) {
            System.out.println(batchBuffer[0][1][i] + ", " + batchBuffer[1][1][i]);
        }

    }

}
