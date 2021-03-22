package net.openrs.cache.xtea;

public class XteaKey {
    private final int region;
    private final int[] keys;

    public XteaKey(int region, int[] keys) {
        this.region = region;
        this.keys = keys;
    }

    public int getRegion() {
        return this.region;
    }

    public int[] getKeys() {
        return this.keys;
    }
}
