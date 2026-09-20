package interfaces.Transition;

public interface AgentTransitionInterface {

    public void setParameters(double[] parameters);

    public double[] getParameters();

    public double[] getNextState(double[][] states, double[][] fields);

    public void getNextState(double[][] state, double[][] fields, double[] nextState);

    public void getBatchNextState(double[][][] states, double[][][] fields, double[][] nextStates);

}
