package com.dogonfire.werewolf;

class Werewolf$7 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.noCheatPlusEnabled) {
            return 1;
        }
        return 0;
    }
}