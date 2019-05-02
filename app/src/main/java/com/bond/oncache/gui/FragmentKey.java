package com.bond.oncache.gui;


public class FragmentKey {
    public final String fragTAG;
    private final int hash;

    public FragmentKey(String fragTAG) {
        this.fragTAG=fragTAG;
        hash = fragTAG.hashCode();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FragmentKey)) return false;
        FragmentKey key = (FragmentKey) o;
        return fragTAG.equals(key.fragTAG);
    }

    @Override
    public int hashCode() {
        /* This is faster than String.hashCode() */
        return hash;
    }
}
