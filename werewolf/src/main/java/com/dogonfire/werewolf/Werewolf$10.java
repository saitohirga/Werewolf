package com.dogonfire.werewolf;

class Werewolf$10 extends Metrics.Plotter {
    @Override
    public int getValue() {
        if (Werewolf.this.renameWerewolves) {
            return 1;
        }
        return 0;
    }
}