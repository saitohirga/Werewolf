package com.dogonfire.werewolf;

import java.util.Comparator;

public class ClanComparator implements Comparator<ClanType>
{
    @Override
    public int compare(final ClanType clan1, final ClanType clan2) {
        return (int)(ClanManager.access$1(ClanManager.this).get(clan2) - ClanManager.access$1(ClanManager.this).get(clan1));
    }
}
