package com.dogonfire.werewolf;

class Werewolf$8 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.wolfChat) {
            return 1;
        }
        return 0;
    }
}