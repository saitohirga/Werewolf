package com.dogonfire.werewolf;

class Werewolf$1 implements Runnable {
    @Override
    public void run() {
        Werewolf.getWerewolfManager().update();
    }
}