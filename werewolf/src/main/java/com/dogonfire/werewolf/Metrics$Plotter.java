package com.dogonfire.werewolf;

public abstract static class Plotter
{
    private final String name;
    
    public Plotter() {
        this("Default");
    }
    
    public Plotter(final String name) {
        super();
        this.name = name;
    }
    
    public abstract int getValue();
    
    public String getColumnName() {
        return this.name;
    }
    
    public void reset() {
    }
    
    @Override
    public int hashCode() {
        return this.getColumnName().hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (!(object instanceof Plotter)) {
            return false;
        }
        final Plotter plotter = (Plotter)object;
        return plotter.name.equals(this.name) && plotter.getValue() == this.getValue();
    }
}
