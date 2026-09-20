package ML.interfaces;

import interfaces.World.WorldInterface;

public interface CostInterface {
    /**
     * Calculates the cost/fitness of a simulation run for a specific agent.
     * @param world The orchestrated world containing the history of the simulation run.
     * @param agentIndex The index of the agent in the DOD batch to evaluate.
     * @return A double representing the cost (lower is typically better).
     */
    public double calculateCost(WorldInterface world, int agentIndex);
}
