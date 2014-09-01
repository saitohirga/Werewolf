package com.dogonfire.werewolf;

class Werewolf$2 implements Runnable {
    @Override
    public void run() {
        Werewolf.getClanManager().updateClans();
    }
}