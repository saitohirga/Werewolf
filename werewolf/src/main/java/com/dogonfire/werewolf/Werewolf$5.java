package com.dogonfire.werewolf;

class Werewolf$5 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.vampireEnabled) {
            return 1;
        }
        return 0;
    }
}