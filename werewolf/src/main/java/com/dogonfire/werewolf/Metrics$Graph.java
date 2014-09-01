package com.dogonfire.werewolf;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public static class Graph
{
    private final String name;
    private final Set<Plotter> plotters;
    
    private Graph(final String name) {
        super();
        this.plotters = new LinkedHashSet<Plotter>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addPlotter(final Plotter plotter) {
        this.plotters.add(plotter);
    }
    
    public void removePlotter(final Plotter plotter) {
        this.plotters.remove(plotter);
    }
    
    public Set<Plotter> getPlotters() {
        return Collections.unmodifiableSet((Set<? extends Plotter>)this.plotters);
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Graph)) {
            return false;
        }
        final Graph graph = (Graph)object;
        return graph.name.equals(this.name);
    }
    
    protected void onOptOut() {
    }
}
