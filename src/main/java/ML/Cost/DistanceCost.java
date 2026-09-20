package ML.Cost;

import ML.interfaces.CostInterface;
import interfaces.World.WorldInterface;

public class DistanceCost implements CostInterface {

    private double[] targetState;
    private double tolerance = 1.0; // Distance to be considered "at target"

    public DistanceCost(double[] targetState) {
        this.targetState = targetState;
    }

    @Override
    public double calculateCost(WorldInterface world, int agentIndex) {
        double[][][] history = world.getFieldDynamicAgentHistory();
        if (history == null || history.length == 0) return Double.MAX_VALUE;

        int maxTicks = history.length - 1;
        int stateDim = targetState.length;

        // 1. Calculate final distance
        double finalDistance = 0.0;
        for (int d = 0; d < stateDim; d++) {
            finalDistance += Math.pow(history[maxTicks][d][agentIndex] - targetState[d], 2);
        }
        finalDistance = Math.sqrt(finalDistance);

        // 2. Calculate time penalty
        // Shortest time is Chebyshev distance (max grid steps in Moore neighborhood)
        double maxDistDim = 0.0;
        for (int d = 0; d < stateDim; d++) {
            maxDistDim = Math.max(maxDistDim, Math.abs(targetState[d] - history[0][d][agentIndex]));
        }
        int optimalTicks = (int) Math.ceil(maxDistDim);

        // Find when it actually reached the target
        int ticksTaken = maxTicks;
        for (int t = 0; t <= maxTicks; t++) {
            double distAtT = 0.0;
            for (int d = 0; d < stateDim; d++) {
                distAtT += Math.pow(history[t][d][agentIndex] - targetState[d], 2);
            }
            distAtT = Math.sqrt(distAtT);
            
            if (distAtT <= tolerance) {
                ticksTaken = t;
                break;
            }
        }

        // Time penalty is how many extra ticks it took beyond the optimal straight line
        double timePenalty = Math.max(0, ticksTaken - optimalTicks);

        // Combined cost: Distance is the primary driver, time is the secondary driver.
        return finalDistance + timePenalty;
    }
}
