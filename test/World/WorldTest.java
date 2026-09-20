package test.World;

import World.World;
import Topology.Topology;
import Generator.FieldGenerator;
import Transition.AgentTransition;
import fields.fieldV1impl.HashTableFieldsImplementation;
import interfaces.field.FieldInterface;

public class WorldTest {

    public static void main(String[] args) {
        System.out.println("--- Testing World Orchestrator Phase 3 Skeleton ---");

        // 1. Initialize empty hollow modules
        Topology topology = new Topology() {
            @Override
            public void getBatchNeighbours(double[][] states, double[][][] stateBuffer) {
                // Hardcode 4 Manhattan neighbors zero-allocation inline
                int numElements = states[0].length;
                for (int i = 0; i < numElements; i++) {
                    double x = states[0][i];
                    double y = states[1][i];
                    // X Dimension
                    stateBuffer[0][i][0] = x + 1;
                    stateBuffer[0][i][1] = x - 1;
                    stateBuffer[0][i][2] = x;
                    stateBuffer[0][i][3] = x;
                    // Y Dimension
                    stateBuffer[1][i][0] = y;
                    stateBuffer[1][i][1] = y;
                    stateBuffer[1][i][2] = y + 1;
                    stateBuffer[1][i][3] = y - 1;
                }
            }
        };

        FieldInterface envFieldStorage = new HashTableFieldsImplementation(100, 2, 1);

        // Put an environmental "obstacle" directly in the path of the Field Dynamic
        // Agent
        // Agent starts at (0.0, 0.0). We put a field blocker at (0.0, 1.0).
        envFieldStorage.put(new double[] { 0.0, 1.0 }, new double[] { -100.0 }); // Massive negative penalty blocks the
                                                                                 // field

        FieldGenerator generator = new FieldGenerator() {

            @Override
            public void generateBatchField(double[][] states, double[][][] neighbourStates, double[][][] fields) {

                int numAgents = states[0].length;
                int numNeighbors = neighbourStates[0][0].length;

                double[] singleState = new double[2]; // X, Y
                double[] envFieldResult = new double[1]; // Scalar field

                for (int i = 0; i < numAgents; i++) {
                    double x = states[0][i];
                    double y = states[1][i];

                    for (int j = 0; j < numNeighbors; j++) {
                        // 1. Pure Mathematical Field: Strictly gradient towards North (higher Y =
                        // stronger field)
                        double pureField = neighbourStates[1][i][j];

                        // 2. Fetch Environmental Field (Filter)
                        singleState[0] = neighbourStates[0][i][j];
                        singleState[1] = neighbourStates[1][i][j];

                        double envMultiplier = 1.0; // Default to unity if no environmental factor exists
                        if (envFieldStorage.get(singleState, envFieldResult)) {
                            envMultiplier = envFieldResult[0]; // If there's an obstacle, it multiplies by -100.0
                                                               // (penalty)
                        }

                        // 3. Component-Wise Multiplication
                        fields[0][i][j] = pureField * envMultiplier;
                    }
                }
            }

        };

        AgentTransition transition = new AgentTransition();

        FieldInterface fieldStorage = new HashTableFieldsImplementation(100, 2, 1);

        // 2. Initialize World
        World world = new World(topology, generator, transition, fieldStorage);

        // 3. Setup Agent Batches (DOD Layout)
        int stateDim = 2; // X, Y

        // A. Field Static Agents
        double[][] fieldStaticAgents = new double[][] {
                { 0.0, 0.0 }, // X coords
                { 0.0, 5.0 } // Y coords
        };
        world.setFieldStaticAgents(fieldStaticAgents);

        // B. Deterministic Agents
        double[][] deterministicAgents = new double[][] {
                { 10.0 }, // X coords
                { 10.0 } // Y coords
        };
        world.setDeterministicDynamicAgents(deterministicAgents);

        // C. Field Dynamic Agents
        double[][] fieldDynamicAgents = new double[][] {
                { 0.0, 10.0 }, // X coords (Agent 1 at 0,0, Agent 2 at 10,10)
                { 0.0, 10.0 } // Y coords
        };
        world.setFieldDynamicAgents(fieldDynamicAgents);

        // 4. Run World tick
        System.out.println("Tick 0 (Before):");
        System.out.println(
                "  Deterministic Agent: (" + deterministicAgents[0][0] + ", " + deterministicAgents[1][0] + ")");
        System.out.println(
                "  Field Dynamic Agent 1: (" + fieldDynamicAgents[0][0] + ", " + fieldDynamicAgents[1][0] + ")");
        System.out.println(
                "  Field Dynamic Agent 2: (" + fieldDynamicAgents[0][1] + ", " + fieldDynamicAgents[1][1] + ")");

        world.tick();

        System.out.println("\nTick 1 (After):");
        System.out.println(
                "  Deterministic Agent: (" + deterministicAgents[0][0] + ", " + deterministicAgents[1][0] + ")");
        System.out.println(
                "  Field Dynamic Agent 1: (" + fieldDynamicAgents[0][0] + ", " + fieldDynamicAgents[1][0] + ")");
        System.out.println(
                "  Field Dynamic Agent 2: (" + fieldDynamicAgents[0][1] + ", " + fieldDynamicAgents[1][1] + ")");
    }
}
