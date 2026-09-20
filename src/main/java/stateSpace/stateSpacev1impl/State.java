package stateSpace.stateSpacev1impl;

import java.util.Arrays;

import interfaces.stateSpace.StateInterface;

public class State implements StateInterface {

    public double[] state;

    public State(double[] state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "State [state=" + Arrays.toString(state) + "]";
    }

}
