# World Orchestrator Guide

The `World` Orchestrator acts as the central engine or "God Loop" of the simulation. Its primary responsibility is to store the simulation state across all agents and orchestrate the flow of data through the mathematical modules (`Topology`, `FieldGenerator`, `AgentTransition`) at every `tick()`.

## 1. Data-Oriented Agent Taxonomy

Instead of creating a traditional Object-Oriented hierarchy (like `abstract class Agent`), the Orchestrator strictly implements **Data-Oriented Design (DOD)** by grouping agents into distinct, primitive 2D arrays based on their behavior.

| Agent Type | DOD Array Variable | Description | Pipeline Processed |
|---|---|---|---|
| **State Static** | `stateStaticAgents` | Exists in space but does not move or emit fields. | Ignored by tick loop. |
| **Field Static** | `fieldStaticAgents` | Emits fields into the world but does not move. | `Topology` ➔ `FieldGenerator` |
| **Deterministic Dynamic** | `deterministicDynamicAgents` | Moves using a fixed, hardcoded mathematical rule. | Bypasses modules ➔ `World.applyDeterministicRules()` |
| **Random Dynamic** | `randomDynamicAgents` | Moves randomly within topology but ignores fields. | `Topology` ➔ `AgentTransition` |
| **Field Dynamic** | `fieldDynamicAgents` | Reads topology and fields to make a policy decision. | `Topology` ➔ `FieldStorage` ➔ `AgentTransition` |

*Note: All 2D agent batch arrays are shaped as `[dimension][numElements]`.*

## 2. Zero-Allocation Bridging (The 3D Arrays)

During the `tick()` cycle, the Orchestrator extracts the topology for a batch of agents. Because each individual agent possesses multiple neighbors, the resulting buffer expands into a **3D Array** shaped as `[dimension][numElements][numNeighbors]`.

This structure is explicitly passed between modules to achieve **absolute zero-allocation execution**:
1. `Topology` receives the 2D agent batch and fills a pre-allocated 3D `stateBuffer` with the neighbors.
2. The Orchestrator queries the `HashTableFieldsImplementation` to fill a 3D `fieldBuffer` correlating to those exact neighbors.
3. `AgentTransition` or `FieldGenerator` receives both 3D buffers in-place to calculate the next state or generated fields, preventing any intermediate array instantiations.

---

## 3. How to Create and Run a World

### Step 1: Initialize the Modules
Instantiate your hollow or mathematical modules.

```java
Topology topology = new Topology(); 
FieldGenerator generator = new FieldGenerator();
AgentTransition transition = new AgentTransition();
HashTableFieldsImplementation fieldStorage = new HashTableFieldsImplementation(100, 2, 1);
```

### Step 2: Initialize the World
Pass the modules into the `World` constructor.

```java
World world = new World(topology, generator, transition, fieldStorage);

// Optional: Configure dimensions
world.setNumNeighbors(4); // e.g., 4 for Manhattan
world.setFieldDim(1);     // e.g., 1 for scalar field
```

### Step 3: Populate the Agent Arrays
Construct your 2D primitive arrays and inject them into the orchestrator for the specific type of agent you are tracking. If an array is empty or `null`, the orchestrator will safely and efficiently bypass its pipeline.

```java
// Example: Create 2 Field Dynamic Agents at (0,0) and (10,10)
double[][] fieldDynamicAgents = new double[][] {
    { 0.0, 10.0 }, // X coordinates
    { 0.0, 10.0 }  // Y coordinates
};
world.setFieldDynamicAgents(fieldDynamicAgents);

// Example: Create 1 Deterministic Agent
double[][] deterministicAgents = new double[][] {
    { 5.0 }, // X coordinate
    { 5.0 }  // Y coordinate
};
world.setDeterministicDynamicAgents(deterministicAgents);
```

### Step 4: Run the Simulation
Execute the tick loop manually, or run for a fixed number of steps.

```java
// Execute exactly 1 tick
world.tick();

// Or execute 1000 ticks continuously
world.run(1000);
```
