package World;

import interfaces.World.WorldInterface;
import interfaces.topology.TopologyInterface;
import interfaces.Generator.FieldGeneratorInterface;
import interfaces.Transition.AgentTransitionInterface;
import interfaces.field.FieldInterface;

public class World implements WorldInterface {

    // DOD Agent Arrays
    // 1. Static Agent (state static)
    private double[][] stateStaticAgents;
    // 2. Field Static Agent
    private double[][] fieldStaticAgents;
    // 3. Deterministic Dynamic Agent
    private double[][] deterministicDynamicAgents;
    // 4. Random Dynamic Agent (Neighbour)
    private double[][] randomDynamicAgents;
    // 5. Field Dynamic Agent
    private double[][] fieldDynamicAgents;
    private double[][] fieldDynamicSystemAgents;

    // History Tracking
    private int currentTick = 0;
    private int maxHistoryTicks = 0;
    private boolean trackingHistory = false;
    private double[][][] fieldDynamicAgentHistory; // [tick][dimension][agentIndex]

    // Modules
    private TopologyInterface topology;
    private FieldGeneratorInterface fieldGenerator;
    private AgentTransitionInterface transition;
    private FieldInterface fieldStorage; // Stores generated fields, etc.
    
    private int numNeighbors = 4; // Hardcoded default for Manhattan for Phase 3
    private int fieldDim = 1;     // Hardcoded default scalar field for Phase 3

    public World(
        TopologyInterface topology,
        FieldGeneratorInterface fieldGenerator,
        AgentTransitionInterface transition,
        FieldInterface fieldStorage
    ) {
        this.topology = topology;
        this.fieldGenerator = fieldGenerator;
        this.transition = transition;
        this.fieldStorage = fieldStorage;
    }

    public void setNumNeighbors(int numNeighbors) {
        this.numNeighbors = numNeighbors;
    }
    
    public void setFieldDim(int fieldDim) {
        this.fieldDim = fieldDim;
    }

    public void setStateStaticAgents(double[][] agents) {
        this.stateStaticAgents = agents;
    }

    public void setFieldStaticAgents(double[][] agents) {
        this.fieldStaticAgents = agents;
    }

    public void setDeterministicDynamicAgents(double[][] agents) {
        this.deterministicDynamicAgents = agents;
    }

    public void setRandomDynamicAgents(double[][] agents) {
        this.randomDynamicAgents = agents;
    }

    public void setFieldDynamicAgents(double[][] agents) {
        this.fieldDynamicAgents = agents;
    }

    @Override
    public void tick() {
        // 1. Process Field Static Agents
        if (fieldStaticAgents != null && fieldStaticAgents.length > 0 && fieldStaticAgents[0].length > 0) {
            int stateDim = fieldStaticAgents.length;
            int numAgents = fieldStaticAgents[0].length;
            
            // Allocate buffers
            double[][][] nextStateBuffer = new double[stateDim][numAgents][numNeighbors];
            double[][][] generatedFields = new double[fieldDim][numAgents][numNeighbors];

            // For field static agents, they just emit fields into their topology.
            topology.getBatchNeighbours(fieldStaticAgents, nextStateBuffer);

            // Generate fields using pure 3D arrays
            fieldGenerator.generateBatchField(fieldStaticAgents, nextStateBuffer, generatedFields);

            // Commit generatedFields to fieldStorage (HashTable)
            double[] singleState = new double[stateDim];
            double[] singleField = new double[fieldDim];
            for (int i = 0; i < numAgents; i++) {
                for (int n = 0; n < numNeighbors; n++) {
                    for (int d = 0; d < stateDim; d++) {
                        singleState[d] = nextStateBuffer[d][i][n];
                    }
                    for (int f = 0; f < fieldDim; f++) {
                        singleField[f] = generatedFields[f][i][n];
                    }
                    fieldStorage.put(singleState, singleField);
                }
            }
        }

        // 2. Process Deterministic Dynamic Agents
        if (deterministicDynamicAgents != null && deterministicDynamicAgents.length > 0 && deterministicDynamicAgents[0].length > 0) {
            applyDeterministicRules(deterministicDynamicAgents);
        }

        // 3. Process Random Dynamic Agents (Neighbour only)
        if (randomDynamicAgents != null && randomDynamicAgents.length > 0 && randomDynamicAgents[0].length > 0) {
            int stateDim = randomDynamicAgents.length;
            int numAgents = randomDynamicAgents[0].length;
            
            double[][][] neighborStates = new double[stateDim][numAgents][numNeighbors];
            // Empty fields for purely random dynamic agents
            double[][][] neighborFields = new double[fieldDim][numAgents][numNeighbors];
            double[][] nextStates = new double[stateDim][numAgents];

            topology.getBatchNeighbours(randomDynamicAgents, neighborStates);
            transition.getBatchNextState(neighborStates, neighborFields, nextStates);

            for (int i = 0; i < numAgents; i++) {
                for (int d = 0; d < stateDim; d++) {
                    randomDynamicAgents[d][i] = nextStates[d][i];
                }
            }
        }

        // 4. Process Field Dynamic Agents
        if (fieldDynamicAgents != null && fieldDynamicAgents.length > 0 && fieldDynamicAgents[0].length > 0) {
            int stateDim = fieldDynamicAgents.length;
            int numAgents = fieldDynamicAgents[0].length;
            
            // Allocate DOD buffers (pure 3D arrays!)
            double[][][] neighborStates = new double[stateDim][numAgents][numNeighbors];
            double[][][] neighborFields = new double[fieldDim][numAgents][numNeighbors];
            double[][] nextStates = new double[stateDim][numAgents];

            // a. Topology fetch
            topology.getBatchNeighbours(fieldDynamicAgents, neighborStates);

            // b. Field fetch 
            double[] singleState = new double[stateDim];
            double[] singleField = new double[fieldDim];
            for (int i = 0; i < numAgents; i++) {
                for (int n = 0; n < numNeighbors; n++) {
                    for (int d = 0; d < stateDim; d++) {
                        singleState[d] = neighborStates[d][i][n];
                    }
                    
                    // Default to 1.0 (Unity) mathematically before fetching
                    for (int f = 0; f < fieldDim; f++) singleField[f] = 1.0; 
                    
                    if (fieldStorage.get(singleState, singleField)) {
                        for (int f = 0; f < fieldDim; f++) {
                            neighborFields[f][i][n] = singleField[f];
                        }
                    } else {
                        // If missing, apply 1.0 to neutralise effect
                        for (int f = 0; f < fieldDim; f++) {
                            neighborFields[f][i][n] = 1.0;
                        }
                    }
                }
            }

            // c. Transition fetch
            transition.getBatchNextState(neighborStates, neighborFields, nextStates);

            // d. Update the state
            for (int i = 0; i < numAgents; i++) {
                for (int d = 0; d < stateDim; d++) {
                    fieldDynamicAgents[d][i] = nextStates[d][i];
                }
            }
        }

        // Record history if enabled
        if (trackingHistory) {
            currentTick++;
            if (currentTick <= maxHistoryTicks && fieldDynamicAgents != null) {
                int stateDim = fieldDynamicAgents.length;
                if (stateDim > 0) {
                    int numAgents = fieldDynamicAgents[0].length;
                    for (int d = 0; d < stateDim; d++) {
                        for (int i = 0; i < numAgents; i++) {
                            fieldDynamicAgentHistory[currentTick][d][i] = fieldDynamicAgents[d][i];
                        }
                    }
                }
            }
        }
    }

    @Override
    public void run(int steps) {
        for (int i = 0; i < steps; i++) {
            tick();
        }
    }

    @Override
    public void enableHistory(int maxTicks) {
        this.maxHistoryTicks = maxTicks;
        this.currentTick = 0;
        this.trackingHistory = true;

        if (fieldDynamicAgents != null && fieldDynamicAgents.length > 0) {
            int stateDim = fieldDynamicAgents.length;
            int numAgents = fieldDynamicAgents[0].length;
            this.fieldDynamicAgentHistory = new double[maxTicks + 1][stateDim][numAgents];
            
            // Record tick 0 (initial state)
            for (int d = 0; d < stateDim; d++) {
                for (int i = 0; i < numAgents; i++) {
                    this.fieldDynamicAgentHistory[0][d][i] = fieldDynamicAgents[d][i];
                }
            }
        }
    }

    @Override
    public double[][][] getFieldDynamicAgentHistory() {
        return this.fieldDynamicAgentHistory;
    }

    /**
     * Simple deterministic rule defined directly in the Orchestrator.
     * Moves all agents +1 in dimension 0.
     */
    private void applyDeterministicRules(double[][] states) {
        int numElements = states[0].length;
        for (int i = 0; i < numElements; i++) {
            states[0][i] += 1.0; 
        }
    }
}
