package Topology;

import interfaces.topology.TopologyInterface;

/**
 * MooreTopology
 * 
 * Returns the 8 adjacent states in a 2D grid.
 */
public class MooreTopology implements TopologyInterface {

    @Override
    public void setParameters(double[] parameters) {}

    @Override
    public double[][] getNeighbourStates(double[] state) {
        double[][] outBuffer = new double[2][8];
        getNeighbourStates(state, outBuffer);
        return outBuffer;
    }

    @Override
    public void getNeighbourStates(double[] state, double[][] neighbourStates) {
        double x = state[0];
        double y = state[1];
        
        // N
        neighbourStates[0][0] = x;
        neighbourStates[1][0] = y + 1.0;
        // NE
        neighbourStates[0][1] = x + 1.0;
        neighbourStates[1][1] = y + 1.0;
        // E
        neighbourStates[0][2] = x + 1.0;
        neighbourStates[1][2] = y;
        // SE
        neighbourStates[0][3] = x + 1.0;
        neighbourStates[1][3] = y - 1.0;
        // S
        neighbourStates[0][4] = x;
        neighbourStates[1][4] = y - 1.0;
        // SW
        neighbourStates[0][5] = x - 1.0;
        neighbourStates[1][5] = y - 1.0;
        // W
        neighbourStates[0][6] = x - 1.0;
        neighbourStates[1][6] = y;
        // NW
        neighbourStates[0][7] = x - 1.0;
        neighbourStates[1][7] = y + 1.0;
    }

    @Override
    public void getBatchNeighbours(double[][] states, double[][][] stateBuffer) {
        int numElements = states[0].length;
        for (int i = 0; i < numElements; i++) {
            double x = states[0][i];
            double y = states[1][i];
            
            // 8 directions (Moore Neighborhood)
            // 0: N (x, y+1)
            // 1: NE (x+1, y+1)
            // 2: E (x+1, y)
            // 3: SE (x+1, y-1)
            // 4: S (x, y-1)
            // 5: SW (x-1, y-1)
            // 6: W (x-1, y)
            // 7: NW (x-1, y+1)
            
            // N
            stateBuffer[0][i][0] = x;
            stateBuffer[1][i][0] = y + 1.0;
            
            // NE
            stateBuffer[0][i][1] = x + 1.0;
            stateBuffer[1][i][1] = y + 1.0;
            
            // E
            stateBuffer[0][i][2] = x + 1.0;
            stateBuffer[1][i][2] = y;
            
            // SE
            stateBuffer[0][i][3] = x + 1.0;
            stateBuffer[1][i][3] = y - 1.0;
            
            // S
            stateBuffer[0][i][4] = x;
            stateBuffer[1][i][4] = y - 1.0;
            
            // SW
            stateBuffer[0][i][5] = x - 1.0;
            stateBuffer[1][i][5] = y - 1.0;
            
            // W
            stateBuffer[0][i][6] = x - 1.0;
            stateBuffer[1][i][6] = y;
            
            // NW
            stateBuffer[0][i][7] = x - 1.0;
            stateBuffer[1][i][7] = y + 1.0;
        }
    }
}
