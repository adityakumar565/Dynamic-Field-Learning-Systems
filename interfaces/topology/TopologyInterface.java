package interfaces.topology;

/**
 * 
 * TopologyInterface
 * 
 * It is used to define the state an agent can transition to from any initial
 * state
 * 
 * From any given initial state it must return the
 * array of states which any agent can transition to.
 * 
 * It must hold an reference to the method which must take input and return some
 * output
 * 
 * Paramters are the oprional values which is used in case when the neighbour
 * list logic depends upon external constants. for exmple in 2d grid it can be
 * distance and in other cases it can be something else.
 */

public interface TopologyInterface {

    /**
     * @param parameters the parameters to be used in the neighbour list logic.
     */
    public void setParameters(double[] parameters);

    /**
     * @param state the initial state
     * @return the array of neighbour states
     */
    public double[][] getNeighbourStates(double[] state);

    /**
     * @param state the initial state
     * @param outBuffer the pre-allocated buffer to store the neighbour states. DOD Layout: [dimension][numNeighbours]
     */
    public void getNeighbourStates(double[] state, double[][] outBuffer);

    /**
     * @param states the batch of initial states. DOD Layout: [dimension][numElements]
     * @param stateBuffer the pre-allocated buffer for neighbours. DOD Layout: [dimension][numElements][numNeighbours]
     */
    public void getBatchNeighbours(double[][] states, double[][][] stateBuffer);

    public default void build(int steps) {

        System.out.print("Building the topology over many lookahead steps");

    }

}
