package com.dogonfire.werewolf;

import java.util.Comparator;

public class TransformationsComparator implements Comparator<String>
{
    @Override
    public int compare(final String member1, final String member2) {
        return Werewolf.getWerewolfManager().getNumberOfTransformations(member2) - Werewolf.getWerewolfManager().getNumberOfTransformations(member1);
    }
}
