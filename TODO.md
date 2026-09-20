# Noisy Forager Simulation - Project TODO List

- [x] **Phase 1: Pure Data (Bridges & Arrays)**
  - [x] Define Bridge data structures (primitive arrays)
  - [x] Implement DOD ECS component arrays (Structure of Arrays)
  - [x] `StateSpace` Module Implementation
  - [x] Zero-Allocation `FieldAlgebra` Math Utility
  - [x] `HashTableFieldsImplementation` (Zero-Allocation Map)
  - [x] Verify mathematical correctness and zero-allocation compliance via `FieldsTest.java`

- [ ] **Phase 2: Hollow Skeletons**
  - [x] Create `Topology` module with hollow `process()` method
  - [x] Create `Generator` (Sensor Model) module with hollow `process(Bridge input, float[] params)` method
  - [x] Create `Transition` (Policy) module with hollow `process(Bridge input, float[] params)` method
  - [ ] Ensure all hollow modules accept `float[]` (or `double[]`) parameter vectors for agnostic optimization

- [ ] **Phase 3: The Orchestrator**
  - [x] Create central `World` loop class (Inner Loop)
  - [x] Initialize hollow modules inside the Orchestrator
  - [x] Implement explicit Bridge buffer passing between modules (no direct method calls)
  - [x] Verify agent blindly marches across the grid without null pointers/memory leaks

- [ ] **Phase 4: Mathematical Implementation**
  - [ ] Replace hollow `Generator` with parameterized noise functions
  - [ ] Replace hollow `Transition` with Bayesian probability update logic
  - [ ] Map static indexes (e.g., `final int IDX_NOISE = 0`) to `float[] params` vectors

- [ ] **Phase 5: The Outer Loop**
  - [ ] Implement `CostEvaluation` module (mathematical loss based on steps and ground truth distance)
  - [ ] Implement `LearningModule` parameter adjustment logic
