package com.glodblock.github.ae2netanalyser.common.me.network.netdata;

public class State<E extends Enum<E>> {

    E state;

    public State(E init) {
        assert init != null;
        this.state = init;
    }

    public void set(E flag) {
        if (flag.ordinal() > this.state.ordinal()) {
            this.state = flag;
        }
    }

    public E get() {
        return this.state;
    }

    @Override
    public int hashCode() {
        return this.state.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof State<?> s) {
            return s.state == this.state;
        }
        return false;
    }

    @Override
    public String toString() {
        return "state[%s]".formatted(this.state);
    }

}
