package com.dogonfire.werewolf;

import java.util.Comparator;

public class TopHuntersComparator implements Comparator<Hunter>
{
    @Override
    public int compare(final Hunter object1, final Hunter object2) {
        return object2.kills - object1.kills;
    }
}
