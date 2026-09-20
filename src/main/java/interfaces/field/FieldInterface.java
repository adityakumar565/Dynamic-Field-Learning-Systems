package interfaces.field;

import interfaces.stateSpace.StateSpaceInterface;

/**
 * 
 * This is a container for the mappings between fields and the state.
 * 
 **/

public interface FieldInterface {

    // Single operations
    public boolean put(double[] state, double[] field);

    public boolean get(double[] state, double[] resultField);

    // Batch Operations using raw arrays
    public void putBatch(double[][] batchStates, double[][] batchFields);

    public void getBatch(double[][] batchStates, double[][] resultFields);

    // Batch Operations using StateSpaceInterface
    public void putBatch(StateSpaceInterface stateSpace, double[][] batchFields);

    public void getBatch(StateSpaceInterface stateSpace, double[][] resultFields);
}
