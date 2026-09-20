# Project Context: The Noisy Forager Simulation

This document serves as the foundational architectural specification and context guide for building "The Noisy Forager." This project merges a Partially Observable Markov Decision Process (POMDP) with a Data-Oriented Design (DOD) Entity Component System (ECS) architecture to create a parameter-tunable learning environment.

## 1. Project Overview & Constraints
* Goal: A "Forager" agent must navigate a fixed 2D grid to locate a hidden, stationary Target. The agent cannot see the target; it only receives noisy distance readings (fields) from its immediate neighborhood (topology). It uses Bayesian dynamic state transitions to calculate the most probable next step.
* Language: Java.
* Memory Constraint (Cache Locality): To mimic high-performance ECS architecture in Java, strictly avoid arrays of objects (e.g., Component[]). Data must be stored in parallel primitive arrays (e.g., float[] position_x, float[] position_y) to ensure memory contiguity.
* Dependency Constraint: Modules are strictly isolated. Modules must never call methods on each other. All data passes through "Bridges" (pure data structs/primitive arrays) routed by a central Orchestrator (World).
* Parameterization: All dynamically assigned functions (Generator, Transition) must accept a raw float[] params vector for their tunable variables to remain optimizer-agnostic.

## 2. Core Architecture

The architecture is divided into an Inner Loop (tick-by-tick simulation) and an Outer Loop (episode evaluation and learning), connected by explicit data structures.

### The Inner Loop (Perception & Execution)
Executes every tick until the Target is found or the time limit expires.
1. State Space: Holds static terrain and dynamic entity positions.
2. Field: Defines the mathematical operations (vector algebra, identity, noise application) over the State Space.
3. Topology: Defines the agent's spatial awareness (e.g., a local window of Up, Down, Left, Right).
4. Generator (Sensor Model): Reads the true Field within the Topology, applies parameterized noise, and outputs a noisy local field.
5. Transition (Policy): Reads the noisy local field, applies Bayesian probability updates based on tunable parameters, and outputs a discrete Action (movement vector).

### The Outer Loop (Learning & Optimization)
Executes only at the end of an episode.
1. Cost Evaluation: Calculates the mathematical loss based on total steps taken and final true distance from the Target (Ground Truth).
2. Learning Module: Reads the loss and adjusts the float[] params used by the Generator (sensor bias/noise) and Transition (belief/action weights).

## 3. Data Flow & Bridge Memory Layouts

Bridges are fixed-size primitive arrays acting as mailboxes. They contain no logic. The central Orchestrator passes these bridges between modules.

* StateField Bridge: [entity_id, pos_x, pos_y, global_field_value]
* StateTop Bridge: [entity_id, pos_x, pos_y]
* TopologyGener Bridge: [neighbor_x, neighbor_y]
* FieldGener Bridge: [true_signal_strength]
* GenTrans Bridge: [neighbor_x, neighbor_y, noisy_signal_value]
* StateCost Bridge: [agent_final_x, agent_final_y, target_true_x, target_true_y]

## 4. State Management & Cache Invalidation

To prevent massive redundant operations on the static 2D grid, state updates utilize a context-aware dirty flag pattern and the Strategy pattern.

* DirtyManager (Base Class): State modules must extend a base class that tracks dirty regions, not just a blind boolean. It holds a bounding box or list of indices (List<Coordinates> dirty_cells).
* Mutation Encapsulation: Modifying state (e.g., updateCell(x, y, value)) must automatically trigger markDirty(x, y) to prevent human error.
* BridgeManager (Interface): A factory/manager that generates bridges. Implementations (e.g., DifferentialBridgeManager or ChunkedBridgeManager) read the dirty_cells payload to only recalculate and pass the specific grid sectors that changed, keeping the rest of the 100x100 grid safely cached.

## 5. Development Phasing (Tracer Bullet Strategy)

When writing code for this project, the AI must follow this strict vertical-slice implementation order:

1. Phase 1: Pure Data (Bridges & Arrays): Define all Bridge data structures (primitive arrays) and ECS component arrays. Do not write module logic yet.
2. Phase 2: Hollow Skeletons: Create the StateSpace, Topology, Generator, and Transition modules. Give them their required execution methods (e.g., process(Bridge input, float[] params)). Hardcode the return values (e.g., Generator always returns 1.0, Transition always returns "Move Right").
3. Phase 3: The Orchestrator: Write the World loop that initializes the hollow modules and explicitly passes the Bridge buffers between them. Verify the agent blindly marches across the grid without null pointers or memory leaks.
4. Phase 4: Mathematical Implementation: Replace the hollow module returns with actual parameterized mathematical logic (Bayesian updates in Transition, noise functions in Generator). Use constant mapping (e.g., final int IDX_NOISE = 0;) to access variables inside float[] params vectors.
5. Phase 5: The Outer Loop: Implement the CostEvaluation ground-truth check and the LearningModule parameter adjustment logic.