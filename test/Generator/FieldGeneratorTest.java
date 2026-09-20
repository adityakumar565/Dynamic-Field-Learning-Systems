package test.Generator;

import Generator.FieldGenerator;

public class FieldGeneratorTest {

    public static void main(String[] args) {
        System.out.println("--- Testing FieldGenerator Phase 2 Skeleton ---");

        FieldGenerator generator = new FieldGenerator(new double[]{ 1.0, 0.5 });
        
        // Setup DOD layout: 2 states, state dimension 2, 4 neighbors per state
        int numStates = 2;
        int stateDim = 2;
        int numNeighbors = 4;
        int fieldDim = 1; // e.g. scalar field output (distance)

        // 2 Initial States: (0,0) and (10,10)
        double[][] initialStates = new double[][] {
            { 0.0, 10.0 }, // X
            { 0.0, 10.0 }  // Y
        };
        
        // Next States shape: [stateDim][numStates][numNeighbors]
        double[][][] nextStates = new double[stateDim][numStates][numNeighbors];
        // We don't need to populate nextStates perfectly because Phase 2 skeleton returns 1.0
        
        // Output Buffer shape: [fieldDim][numStates][numNeighbors]
        double[][][] outputBuffer = new double[fieldDim][numStates][numNeighbors];

        generator.generateBatchField(initialStates, nextStates, outputBuffer);

        System.out.println("Output Buffer (State 1, Neighbors 0-3):");
        for (int n = 0; n < numNeighbors; n++) {
            System.out.println("Neighbor " + n + " field value: " + outputBuffer[0][0][n]);
        }

        System.out.println("\nOutput Buffer (State 2, Neighbors 0-3):");
        for (int n = 0; n < numNeighbors; n++) {
            System.out.println("Neighbor " + n + " field value: " + outputBuffer[0][1][n]);
        }
    }
    
}
