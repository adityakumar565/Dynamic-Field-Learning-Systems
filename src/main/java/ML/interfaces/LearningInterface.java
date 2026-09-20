package ML.interfaces;

import interfaces.World.WorldInterface;
import interfaces.Generator.FieldGeneratorInterface;
import interfaces.Transition.AgentTransitionInterface;

public interface LearningInterface {
    /**
     * Optimizes the parameters of the given generator and transition modules.
     * @param world The orchestrated world to run simulations in.
     * @param generator The field generator containing generation parameters to optimize.
     * @param transition The agent transition containing transition parameters to optimize.
     * @param costFunction The function used to evaluate the fitness of the parameters.
     * @return the best cost found in this optimization step.
     */
    public double optimize(WorldInterface world, FieldGeneratorInterface generator, AgentTransitionInterface transition, CostInterface costFunction);
}
