package com.dogonfire.werewolf;

class Werewolf$4 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.vaultEnabled) {
            return 1;
        }
        return 0;
    }
}