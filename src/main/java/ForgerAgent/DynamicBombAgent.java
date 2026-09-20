package ForgerAgent;

import Topology.MooreTopology;
import World.World;

public class DynamicBombAgent {

    public static void main(String[] args) {
        System.out.println("--- Starting Dynamic Bomb Evolutionary Simulation ---");

        int numAgents = 100;
        int stateDim = 2;
        int maxTicks = 200;
        int numEpochs = 50;

        // Mutable bomb location array so we can change it each epoch
        final double[] currentBombLoc = new double[]{0.0, 0.0};

        // 1. Topology
        Topology.MooreTopology topology = new Topology.MooreTopology();

        // 2. The Bomb's Environmental Field (Dynamically evaluated linear drop-off)
        interfaces.field.FieldInterface bombEnvStorage = new fields.fieldV1impl.HashTableFieldsImplementation(1, 2, 1) {
            double maxRange = 300.0; // Larger range so agents can always sense the gradient

            @Override
            public boolean get(double[] state, double[] resultField) {
                double dist = Math.sqrt(Math.pow(state[0] - currentBombLoc[0], 2) + Math.pow(state[1] - currentBombLoc[1], 2));
                resultField[0] = Math.max(0.0, maxRange - dist);
                return true; 
            }
        };

        // 3. Field Generator (Encapsulates Generation Parameters for ALL agents)
        Generator.FieldGenerator generator = new Generator.FieldGenerator() {
            @Override
            public void generateBatchField(double[][] states, double[][][] neighbourStates, double[][][] fields) {
                double[] genParams = getParameters(); 
                int numAgents = states[0].length;
                int numNeighbors = neighbourStates[0][0].length;
                
                double[] singleState = new double[2];
                double[] envFieldResult = new double[1];

                for (int i = 0; i < numAgents; i++) {
                    for (int n = 0; n < numNeighbors; n++) {
                        singleState[0] = neighbourStates[0][i][n];
                        singleState[1] = neighbourStates[1][i][n];
                        
                        double envSignal = 0.0;
                        if (bombEnvStorage.get(singleState, envFieldResult)) {
                            envSignal = envFieldResult[0];
                        }
                        
                        // Modulate the env field with gen parameters
                        fields[0][i][n] = envSignal * genParams[i * 8 + n];
                    }
                }
            }
        };
        
        double[] initialGenParams = new double[numAgents * 8];
        for (int i = 0; i < initialGenParams.length; i++) initialGenParams[i] = Math.random() * 2.0 - 1.0;
        generator.setParameters(initialGenParams);

        // 4. Agent Transition
        Transition.AgentTransition transition = new Transition.AgentTransition() {
            @Override
            public void getBatchNextState(double[][][] states, double[][][] fields, double[][] nextStates) {
                double[] transParams = getParameters(); 
                int stateDim = states.length;
                int numElements = states[0].length;
                int numNeighbors = states[0][0].length;

                double[][][] generatedFields = new double[1][numElements][numNeighbors];
                double[][] dummyInitialStates = new double[stateDim][numElements];
                generator.generateBatchField(dummyInitialStates, states, generatedFields);

                for (int i = 0; i < numElements; i++) {
                    int bestNeighborIndex = 0;
                    double maxField = -Double.MAX_VALUE;

                    for (int n = 0; n < numNeighbors; n++) {
                        double evaluatedField = generatedFields[0][i][n] * transParams[i * 8 + n];

                        if (evaluatedField > maxField) {
                            maxField = evaluatedField;
                            bestNeighborIndex = n;
                        }
                    }

                    for (int d = 0; d < stateDim; d++) {
                        nextStates[d][i] = states[d][i][bestNeighborIndex];
                    }
                }
            }
        };
        
        double[] initialTransParams = new double[numAgents * 8];
        for (int i = 0; i < initialTransParams.length; i++) initialTransParams[i] = Math.random() * 2.0 - 1.0;
        transition.setParameters(initialTransParams);

        World world = new World(topology, generator, transition, bombEnvStorage);
        world.setNumNeighbors(8);

        double[][] forgerAgents = new double[][] {
            new double[numAgents],
            new double[numAgents]
        };
        
        ML.interfaces.LearningInterface optimizer = new ML.Learning.EvolutionaryOptimizer(10, 0.2);

        // Setup timestamped data folder
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String runFolder = "data/runs_dynamic/" + timestamp;
        String csvFolder = runFolder + "/csv";
        System.out.println("Data will be exported to: " + runFolder);

        // Evolutionary Epoch Loop
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            
            // RANDOMIZE BOMB LOCATION EACH EPOCH! (Between -50 and 50)
            currentBombLoc[0] = Math.random() * 100.0 - 50.0;
            currentBombLoc[1] = Math.random() * 100.0 - 50.0;
            
            // Create a NEW Cost Function specific to this epoch's bomb location
            ML.interfaces.CostInterface costFunction = new ML.Cost.DistanceCost(new double[]{currentBombLoc[0], currentBombLoc[1]});

            System.out.printf("Epoch %d [Bomb @ (%.1f, %.1f)] -> ", epoch, currentBombLoc[0], currentBombLoc[1]);
            
            // Reset all agents to origin
            for (int i = 0; i < numAgents; i++) {
                forgerAgents[0][i] = 0.0;
                forgerAgents[1][i] = 0.0;
            }
            world.setFieldDynamicAgents(forgerAgents);

            // Run Simulation
            world.enableHistory(maxTicks);
            world.run(maxTicks);

            // Optimize
            double bestCost = optimizer.optimize(world, generator, transition, costFunction);

            Visualizer.DataExporter.exportEpoch(epoch, world.getFieldDynamicAgentHistory(), csvFolder);
            Visualizer.DataExporter.appendCost(epoch, bestCost, csvFolder);
            Visualizer.DataExporter.exportTarget(epoch, currentBombLoc[0], currentBombLoc[1], csvFolder);
        }
        
        System.out.println("Simulation complete. Generating plots...");
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "Visualizer/plot_trajectories.py", runFolder);
            pb.inheritIO();
            Process p = pb.start();
            p.waitFor();
            System.out.println("Plots generated successfully.");
        } catch (Exception e) {
            System.out.println("Failed to run python visualizer automatically: " + e.getMessage());
            System.out.println("You can manually generate them by running: python Visualizer/plot_trajectories.py");
        }
    }
}
