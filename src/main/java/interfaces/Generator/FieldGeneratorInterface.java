package interfaces.Generator;

/**
 * 
 * FieldGeneratorInterface
 * 
 * Given the initial state and the next transition state, based on the
 * parameters
 * , this is used to generate the fields to the next state
 */

public interface FieldGeneratorInterface {

    public void setParameters(double[] parameters);

    public double[] getParameters();

    public void generateFields(double[] intialState, double[] nextState, double[] outputBuffer);

    public double[] generateFields(double[] intialState, double[] nextState);

    /**
     * @param intialState
     * @param nextState
     * @param outputBuffer
     */
    public void generateBatchField(double[][] intialState, double[][][] nextState, double[][][] outputBuffer);

}
