package com.dogonfire.werewolf;

class Werewolf$6 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.antiCheatEnabled) {
            return 1;
        }
        return 0;
    }
}