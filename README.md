# AI Forger Agent (v1.0 Prototype)

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://java.com/)
[![Python](https://img.shields.io/badge/Python-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://python.org/)
[![Data-Oriented Design](https://img.shields.io/badge/Architecture-DOD-red?style=for-the-badge)](#)

A blisteringly fast, data-oriented physics simulation and planning engine designed to optimize continuous state-space traversal using Machine Learning.

This prototype demonstrates a highly-scalable, cache-friendly architecture capable of simulating hundreds of thousands of agents in real-time. By leveraging **Data-Oriented Design (DOD)** and flattening nested objects into pure contiguous memory arrays (`double[][][]`), this engine entirely bypasses traditional OOP overhead. 

While currently demonstrated as a 2D physical pathfinding solver (seeking a target coordinate), the engine is completely generic. Through **Representation Theory**, discrete actions can be embedded into continuous vector spaces, turning this engine into a powerful Continuous Planning, Routing, or Model-Based Reinforcement Learning solver.

---

## 🎯 Demo & Evidence

When hooked up to the internal `EvolutionaryOptimizer`, a batch of 100 random agents quickly converges on the optimal path to a designated goal state across 50 epochs. 

*Below: The trajectory of the best agent over 50 epochs. Note how the agent quickly evolves from erratic guessing to a mathematically optimal straight line to the target (the star).*

![Agent Evolution](docs/assets/best_agents_evolution.png)

*The cost function strictly minimizing to zero over the course of the training epochs.*

![Optimization Curve](docs/assets/optimization_curve.png)

---

## 🏗️ Architecture

The system is strictly decoupled into three overarching layers:

1. **Simulation Engine (Java)**: The core processing loop. Extremely fast, zero-allocation during execution.
2. **Machine Learning Optimizer (Java)**: A separate module that evaluates Cost Functions and applies Elitism/Mutation across simulation epochs.
3. **Visualizer (Python)**: Reads offline `.csv` dumps to render Matplotlib analytics.

### Core Simulation Components

The internal tick loop is driven by the `World` orchestrator, passing contiguous memory blocks through four mathematically rigorous phases:

1. **State Space**: Represents the current continuous states of all agents (e.g., `X, Y` coordinates, or complex action embeddings).
2. **Topology**: The rule engine. For a given state, it generates all valid adjacent "Next States" (or valid subsequent actions). Currently implemented as a Moore Neighborhood topology.
3. **Field Generator**: Evaluates environmental signals (like distance to a target/bomb) at the neighbor coordinates and modulates them against the agent's internal Generation Weights. 
4. **Agent Transition**: Aggregates the generated fields against the agent's Transition Weights to select the absolute best adjacent state, stepping the simulation forward.

Because these operations are purely array transformations, they are ripe for SIMD vectorization and GPU acceleration.

---

## 💻 Code Example

Setting up and running the simulation requires composing the rules and firing the batch orchestrator:

```java
// 1. Define the Environmental rules (e.g. Distance to target)
FieldInterface targetEnv = new HashTableFieldsImplementation(...) {
    public boolean get(double[] state, double[] resultField) {
        // Calculate distance gradient
    }
};

// 2. Setup the Data-Oriented Components
Topology topology = new MooreTopology();
FieldGenerator generator = new FieldGenerator() { ... };
AgentTransition transition = new AgentTransition() { ... };

// 3. Initialize the World
World world = new World(topology, generator, transition, targetEnv);
world.setFieldDynamicAgents(initialStates);

// 4. Train the Agents using ML
LearningInterface optimizer = new EvolutionaryOptimizer(10, 0.2);
for (int epoch = 1; epoch <= 50; epoch++) {
    world.run(200); // 200 ticks
    double bestCost = optimizer.optimize(world, generator, transition, distanceCostFunction);
}
```

---

## 🚀 Setup & Execution

### Prerequisites
* Java JDK (8+)
* Python 3.8+ (with `pandas` and `matplotlib` installed)

### Running the Engine

We have provided a cross-platform compilation script that ensures your source tree stays clean by compiling to a hidden `out/` folder, running the Java engine, and automatically kicking off the Python visualizer.

**Windows (PowerShell):**
```powershell
# Run the static target learning simulation
.\compile_and_run.ps1

# Run the dynamic/moving target simulation (investigatory mode)
.\compile_and_run.ps1 -agent "ForgerAgent.DynamicBombAgent"
```

The output data will be neatly saved into timestamped folders in the `data/` directory (e.g., `data/runs/20260920_223000/`), containing both the raw `csv/` telemetry and the rendered `plots/`.

---

## 🔮 Scope & Future Work (V2.0)

This V1.0 Prototype successfully validated the mathematical limits and performance of the array-based batch architecture. The immediate next steps for the project include:

1. **Bridge Data Structures (V2.0)**: 
   The current prototype hardcodes the dimensionality of the state arrays (e.g. `double[stateDim][numElements][numNeighbors]`). We will introduce heavily decoupled "Bridge" data structures to allow totally dynamic scaling of dimensional requirements without losing cache locality.
2. **C++ / GPU Compute Integration**: 
   Because the core mathematical loop is already perfectly vectorized, the `Generator` and `Transition` layers will be rewritten as CUDA or OpenCL kernels. This will push the engine's capability from 50,000 agents up into the millions.
3. **Advanced ML Topologies**: 
   Implementation of Recurrent Network structures within the Transition layer to allow agents to handle non-Markovian decision making across dynamically moving targets.
