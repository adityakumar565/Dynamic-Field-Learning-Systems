package interfaces.stateSpace;

public interface StateSpaceInterface {

    // Primitive data passing (NO StateInterface objects)
    public boolean addState(double[] state);

    public boolean contains(double[] state);

    // Operations based on another StateSpaceInterface
    public void inPlaceUnion(StateSpaceInterface stateSpace);

    public StateSpaceInterface union(StateSpaceInterface stateSpace);

    public void inPlaceIntersection(StateSpaceInterface stateSpace);

    public StateSpaceInterface intersection(StateSpaceInterface stateSpace);

    // Batch Operations based on raw double[][] arrays (DOD structure: [dimension][numStates])
    public void inPlaceUnion(double[][] batchStates);

    public StateSpaceInterface union(double[][] batchStates);

    public void inPlaceIntersection(double[][] batchStates);

    public StateSpaceInterface intersection(double[][] batchStates);
}
