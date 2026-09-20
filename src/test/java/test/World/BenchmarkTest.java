package test.World;

import World.World;
import Topology.Topology;
import Generator.FieldGenerator;
import Transition.AgentTransition;
import fields.fieldV1impl.HashTableFieldsImplementation;
import interfaces.field.FieldInterface;

public class BenchmarkTest {

    public static void main(String[] args) {
        System.out.println("--- Benchmarking World Orchestrator (100,000 Agents) ---");

        int numAgents = 40000;
        int stateDim = 2;

        Topology topology = new Topology() {
            @Override
            public void getBatchNeighbours(double[][] states, double[][][] stateBuffer) {
                int nElements = states[0].length;
                for (int i = 0; i < nElements; i++) {
                    double x = states[0][i];
                    double y = states[1][i];
                    stateBuffer[0][i][0] = x + 1;
                    stateBuffer[0][i][1] = x - 1;
                    stateBuffer[0][i][2] = x;
                    stateBuffer[0][i][3] = x;
                    stateBuffer[1][i][0] = y;
                    stateBuffer[1][i][1] = y;
                    stateBuffer[1][i][2] = y + 1;
                    stateBuffer[1][i][3] = y - 1;
                }
            }
        };

        FieldGenerator generator = new FieldGenerator();
        AgentTransition transition = new AgentTransition();

        // Very large hash table to minimize collisions if we actually populated it
        FieldInterface fieldStorage = new HashTableFieldsImplementation(500000, 2, 1);

        World world = new World(topology, generator, transition, fieldStorage);

        double[][] dynamicAgents = new double[stateDim][numAgents];
        for (int i = 0; i < numAgents; i++) {
            dynamicAgents[0][i] = (int) (Math.random() * 1000);
            dynamicAgents[1][i] = (int) (Math.random() * 1000);
        }
        world.setFieldDynamicAgents(dynamicAgents);

        System.out.println("Warming up JVM (JIT Compilation)...");
        for (int i = 0; i < 50; i++) {
            world.tick();
        }

        System.out.println("Starting Benchmark...");
        int frames = 600; // 10 seconds of 60 FPS
        long startTime = System.nanoTime();
        for (int i = 0; i < frames; i++) {
            world.tick();
        }
        long endTime = System.nanoTime();

        double timeMs = (endTime - startTime) / 1000000.0;
        double msPerTick = timeMs / frames;

        System.out.println("Total time for " + frames + " ticks: " + timeMs + " ms");
        System.out.println("Average time per tick: " + msPerTick + " ms");
        System.out.println("Target for 60 FPS: 16.66 ms");

        if (msPerTick <= 16.66) {
            System.out.println("Status: EXCELLENT (Hit 60 FPS target)");
        } else {
            System.out.println("Status: NEEDS OPTIMIZATION (Missed 60 FPS target)");
        }
    }
}
