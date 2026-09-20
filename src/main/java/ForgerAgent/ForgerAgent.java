package ForgerAgent;

public class ForgerAgent {

    public static void main(String[] args) {
        System.out.println("--- Starting Forger Agent Evolutionary Simulation ---");

        int numAgents = 100;
        int stateDim = 2;
        int maxTicks = 200;
        int numEpochs = 50;

        // 1. Topology
        Topology.MooreTopology topology = new Topology.MooreTopology();

        // 2. The Bomb's Environmental Field (Dynamically evaluated linear drop-off)
        interfaces.field.FieldInterface bombEnvStorage = new fields.fieldV1impl.HashTableFieldsImplementation(1, 2, 1) {
            double bombX = 50.0;
            double bombY = 50.0;
            double maxRange = 100.0;

            @Override
            public boolean get(double[] state, double[] resultField) {
                double dist = Math.sqrt(Math.pow(state[0] - bombX, 2) + Math.pow(state[1] - bombY, 2));
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
                        
                        // g(s_n|s_0; params): Modulate the env field with gen parameters (flattened lookup)
                        fields[0][i][n] = envSignal * genParams[i * 8 + n];
                    }
                }
            }
        };
        
        double[] initialGenParams = new double[numAgents * 8];
        for (int i = 0; i < initialGenParams.length; i++) initialGenParams[i] = Math.random() * 2.0 - 1.0;
        generator.setParameters(initialGenParams);

        // 4. Agent Transition (Encapsulates Transition Parameters for ALL agents)
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
                        // t(state, field; params): Evaluate generated perception for move (flattened lookup)
                        double evaluatedField = generatedFields[0][i][n] * transParams[i * 8 + n];

                        if (evaluatedField > maxField) {
                            maxField = evaluatedField;
                            bestNeighborIndex = n;
                        }
                    }

                    // Select the state of the best neighbor
                    for (int d = 0; d < stateDim; d++) {
                        nextStates[d][i] = states[d][i][bestNeighborIndex];
                    }
                }
            }
        };
        
        double[] initialTransParams = new double[numAgents * 8];
        for (int i = 0; i < initialTransParams.length; i++) initialTransParams[i] = Math.random() * 2.0 - 1.0;
        transition.setParameters(initialTransParams);

        // Initialize World
        World.World world = new World.World(topology, generator, transition, bombEnvStorage);
        world.setNumNeighbors(8);

        // Initialize Agents
        double[][] forgerAgents = new double[][] {
            new double[numAgents],
            new double[numAgents]
        };
        
        ML.interfaces.CostInterface costFunction = new ML.Cost.DistanceCost(new double[]{50.0, 50.0});
        ML.interfaces.LearningInterface optimizer = new ML.Learning.EvolutionaryOptimizer(10, 0.2);

        // Setup timestamped data folder
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String runFolder = "data/runs/" + timestamp;
        String csvFolder = runFolder + "/csv";

        System.out.println("Bomb Location: (50.0, 50.0)");
        System.out.println("Starting Distance: " + Math.sqrt(Math.pow(0 - 50, 2) + Math.pow(0 - 50, 2)));
        System.out.println("Data will be exported to: " + runFolder);

        // Evolutionary Epoch Loop
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            System.out.print("Epoch " + epoch + " -> ");
            
            // 1. Reset all agents to origin
            for (int i = 0; i < numAgents; i++) {
                forgerAgents[0][i] = 0.0;
                forgerAgents[1][i] = 0.0;
            }
            world.setFieldDynamicAgents(forgerAgents);

            // 2. Run Simulation
            world.enableHistory(maxTicks);
            world.run(maxTicks);

            // 3. Optimize (Evaluates cost, prints best, applies elitism & mutation)
            double bestCost = optimizer.optimize(world, generator, transition, costFunction);

            // 4. Export Data
            Visualizer.DataExporter.exportEpoch(epoch, world.getFieldDynamicAgentHistory(), csvFolder);
            Visualizer.DataExporter.appendCost(epoch, bestCost, csvFolder);
            Visualizer.DataExporter.exportTarget(epoch, 50.0, 50.0, csvFolder);
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
