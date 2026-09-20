package test.Transition;

import Transition.AgentTransition;

public class AgentTransitionTest {

    public static void main(String[] args) {
        System.out.println("--- Testing AgentTransition Phase 2 Skeleton ---");

        AgentTransition transition = new AgentTransition(new double[] { 1.0 });

        // Setup DOD layout: 2 agents, state dimension 2, 4 neighbors, field dimension 1
        int numAgents = 2;
        int stateDim = 2;
        int numNeighbors = 4;
        int fieldDim = 1;

        double[][][] neighborStates = new double[stateDim][numAgents][numNeighbors];

        // Agent 1 neighbors (let's say its first neighbor is at X=5, Y=5)
        neighborStates[0][0][0] = 5.0; // X
        neighborStates[1][0][0] = 5.0; // Y

        // Agent 2 neighbors (let's say its first neighbor is at X=10, Y=10)
        neighborStates[0][1][0] = 10.0; // X
        neighborStates[1][1][0] = 10.0; // Y

        double[][][] neighborFields = new double[fieldDim][numAgents][numNeighbors];
        // Don't need to populate fields for the Phase 2 skeleton

        // Output Buffer shape: [stateDim][numAgents]
        double[][] nextStatesBuffer = new double[stateDim][numAgents];

        // Execute batch transition
        transition.getBatchNextState(neighborStates, neighborFields, nextStatesBuffer);

        System.out.println("Agent 1 Selected Next State (Expected 5.0, 5.0): " + nextStatesBuffer[0][0] + ", "
                + nextStatesBuffer[1][0]);
        System.out.println("Agent 2 Selected Next State (Expected 10.0, 10.0): " + nextStatesBuffer[0][1] + ", "
                + nextStatesBuffer[1][1]);
    }
}
