package com.dogonfire.werewolf;

import net.minecraft.util.org.apache.commons.lang3.ObjectUtils;
import net.minecraft.server.v1_7_R1.WatchableObject;
import net.minecraft.server.v1_7_R1.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.server.v1_7_R1.DataWatcher;

public class WerewolfDataWatcher extends DataWatcher
{
    static Method iMethod;
    static Field eBoolean;
    
    static {
        try {
            (WerewolfDataWatcher.iMethod = DataWatcher.class.getDeclaredMethod("i", Integer.TYPE)).setAccessible(true);
        }
        catch (Exception ex) {}
        try {
            (WerewolfDataWatcher.eBoolean = DataWatcher.class.getDeclaredField("e")).setAccessible(true);
        }
        catch (Exception ex2) {}
    }
    
    public WerewolfDataWatcher(final Entity arg0) {
        super(arg0);
    }
    
    public void watch(final int paramInt, final Object paramObject) {
        WatchableObject localWatchableObject = null;
        try {
            localWatchableObject = (WatchableObject)WerewolfDataWatcher.iMethod.invoke(this, paramInt);
        }
        catch (Exception ex) {}
        if (ObjectUtils.notEqual(paramObject, localWatchableObject.b())) {
            localWatchableObject.a(paramObject);
            localWatchableObject.a(true);
            try {
                WerewolfDataWatcher.eBoolean.setBoolean(this, true);
            }
            catch (Exception ex2) {}
        }
    }
}
