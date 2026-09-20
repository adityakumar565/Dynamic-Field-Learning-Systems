package interfaces.World;

public interface WorldInterface {

    /**
     * Executes a single simulation tick over all agent batches.
     */
    public void tick();

    /**
     * Executes the simulation tick for a specified number of steps.
     * @param steps number of ticks to execute
     */
    public void run(int steps);

    /**
     * Pre-allocates memory to track the history of the Field Dynamic Agents over the specified number of ticks.
     * @param maxTicks the maximum number of ticks to track
     */
    public void enableHistory(int maxTicks);

    /**
     * Retrieves the stored history of the Field Dynamic Agents.
     * DOD Layout: [tick][dimension][numElements]
     * Index 0 is the initial state before any ticks.
     * @return the massive DOD history buffer
     */
    public double[][][] getFieldDynamicAgentHistory();
}
