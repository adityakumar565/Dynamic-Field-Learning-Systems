package ML.Learning;

import ML.interfaces.LearningInterface;
import ML.interfaces.CostInterface;
import interfaces.World.WorldInterface;
import interfaces.Generator.FieldGeneratorInterface;
import interfaces.Transition.AgentTransitionInterface;
import java.util.Arrays;

public class EvolutionaryOptimizer implements LearningInterface {

    private int elitismCount;
    private double mutationRate;

    public EvolutionaryOptimizer(int elitismCount, double mutationRate) {
        this.elitismCount = elitismCount;
        this.mutationRate = mutationRate;
    }

    private static class AgentCost implements Comparable<AgentCost> {
        int index;
        double cost;

        AgentCost(int index, double cost) {
            this.index = index;
            this.cost = cost;
        }

        @Override
        public int compareTo(AgentCost other) {
            return Double.compare(this.cost, other.cost);
        }
    }

    @Override
    public double optimize(WorldInterface world, FieldGeneratorInterface generator, AgentTransitionInterface transition, CostInterface costFunction) {
        double[] currentGenParams = generator.getParameters();
        double[] currentTransParams = transition.getParameters();
        
        int numAgents = currentGenParams.length / 8;
        AgentCost[] costs = new AgentCost[numAgents];

        // 1. Evaluate all agents
        for (int i = 0; i < numAgents; i++) {
            costs[i] = new AgentCost(i, costFunction.calculateCost(world, i));
        }

        // 2. Sort by lowest cost
        Arrays.sort(costs);
        
        System.out.println("Generation Best Cost: " + costs[0].cost);

        double[] nextGenParams = new double[currentGenParams.length];
        double[] nextTransParams = new double[currentTransParams.length];

        // 3. Elitism (Keep top N exactly as they are)
        int actualElitism = Math.min(elitismCount, numAgents);
        for (int i = 0; i < actualElitism; i++) {
            int originalIndex = costs[i].index;
            for (int n = 0; n < 8; n++) {
                nextGenParams[i * 8 + n] = currentGenParams[originalIndex * 8 + n];
                nextTransParams[i * 8 + n] = currentTransParams[originalIndex * 8 + n];
            }
        }

        // 4. Mutation (Generate the rest by mutating the elites)
        for (int i = actualElitism; i < numAgents; i++) {
            // Pick a parent from the elite group (round robin)
            int parentRank = i % actualElitism;
            int parentIndex = costs[parentRank].index;

            for (int n = 0; n < 8; n++) {
                // Random variation between -mutationRate and +mutationRate
                double gMut = (Math.random() * 2.0 - 1.0) * mutationRate;
                double tMut = (Math.random() * 2.0 - 1.0) * mutationRate;
                
                nextGenParams[i * 8 + n] = currentGenParams[parentIndex * 8 + n] + gMut;
                nextTransParams[i * 8 + n] = currentTransParams[parentIndex * 8 + n] + tMut;
            }
        }

        // 5. Update engine with new generation
        generator.setParameters(nextGenParams);
        transition.setParameters(nextTransParams);
        
        return costs[0].cost;
    }
}
