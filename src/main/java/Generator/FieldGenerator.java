package Generator;

import interfaces.Generator.FieldGeneratorInterface;

public class FieldGenerator implements FieldGeneratorInterface {

    private double[] parameters;

    public FieldGenerator() {
        this.parameters = null;
    }

    public FieldGenerator(double[] parameters) {
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
    public void generateFields(double[] intialState, double[] nextState, double[] outputBuffer) {
        // Hollow Skeleton Phase 2: return 1.0 for all field components
        for (int i = 0; i < outputBuffer.length; i++) {
            outputBuffer[i] = 1.0;
        }
    }

    @Override
    public double[] generateFields(double[] intialState, double[] nextState) {
        // Hollow Skeleton Phase 2: return a mock field array
        return new double[] { 1.0 };
    }

    /**
     * @param intialState
     * @param nextState
     * @param outputBuffer
     */
    public void generateBatchField(double[][] intialState, double[][][] nextState, double[][][] outputBuffer) {
        int stateDim = intialState.length;
        if (stateDim == 0) return;
        int numElements = intialState[0].length;
        if (numElements == 0) return;

        int numNeighbors = nextState[0][0].length;
        int fieldDim = outputBuffer.length;

        // Allocate temporary buffers once per batch call
        double[] singleInitial = new double[stateDim];
        double[] singleNext = new double[stateDim];
        double[] singleOutput = new double[fieldDim];

        for (int i = 0; i < numElements; i++) {
            // 1. Extract single initial state from DOD arrays
            for (int d = 0; d < stateDim; d++) {
                singleInitial[d] = intialState[d][i];
            }

            for (int n = 0; n < numNeighbors; n++) {
                // 2. Extract single next state
                for (int d = 0; d < stateDim; d++) {
                    singleNext[d] = nextState[d][i][n];
                }

                // 3. Generate field
                generateFields(singleInitial, singleNext, singleOutput);

                // 4. Insert into massive DOD batch buffer
                for (int f = 0; f < fieldDim; f++) {
                    outputBuffer[f][i][n] = singleOutput[f];
                }
            }
        }
    }

}
