package com.dogonfire.werewolf;

import java.util.Comparator;

public class ClanMemberComparator implements Comparator<String>
{
    @Override
    public int compare(final String member1, final String member2) {
        return (int)(ClanManager.access$0(ClanManager.this).get(member2) - ClanManager.access$0(ClanManager.this).get(member1));
    }
}
