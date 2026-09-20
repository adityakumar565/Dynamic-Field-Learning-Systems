package test.stateSpace;

import interfaces.stateSpace.StateSpaceInterface;
import stateSpace.stateSpacev1impl.StateSpace;

public class StateSpaceTest {

    public static void main(String[] args) {
        System.out.println("--- Starting StateSpace Tests ---");

        // 1. Initialize a 2-Dimensional state space that can hold up to 100 states
        StateSpaceInterface stateSpace = new StateSpace(2, 100);

        // 2. Add some states
        stateSpace.addState(new double[]{10.0, 5.0});
        stateSpace.addState(new double[]{15.0, 7.0});

        // 3. Test the contains() method
        System.out.println("Contains [10.0, 5.0]? Expected true -> " + stateSpace.contains(new double[]{10.0, 5.0}));
        System.out.println("Contains [0.0, 0.0]? Expected false -> " + stateSpace.contains(new double[]{0.0, 0.0}));

        // 4. Create a second state space to test Set Operations
        StateSpaceInterface otherSpace = new StateSpace(2, 100);
        otherSpace.addState(new double[]{15.0, 7.0}); // This overlaps with the first state space
        otherSpace.addState(new double[]{20.0, 8.0}); // This is unique

        System.out.println("\n--- Testing Union ---");
        StateSpaceInterface unionSpace = stateSpace.union(otherSpace);
        System.out.println("Union contains [10.0, 5.0]? Expected true -> " + unionSpace.contains(new double[]{10.0, 5.0}));
        System.out.println("Union contains [15.0, 7.0]? Expected true -> " + unionSpace.contains(new double[]{15.0, 7.0}));
        System.out.println("Union contains [20.0, 8.0]? Expected true -> " + unionSpace.contains(new double[]{20.0, 8.0}));

        System.out.println("\n--- Testing Intersection ---");
        StateSpaceInterface intersectSpace = stateSpace.intersection(otherSpace);
        System.out.println("Intersection contains [10.0, 5.0]? Expected false -> " + intersectSpace.contains(new double[]{10.0, 5.0}));
        System.out.println("Intersection contains [15.0, 7.0]? Expected true -> " + intersectSpace.contains(new double[]{15.0, 7.0}));
        System.out.println("Intersection contains [20.0, 8.0]? Expected false -> " + intersectSpace.contains(new double[]{20.0, 8.0}));
    }
}
