package net.openrs.cache.skeleton;

import java.nio.ByteBuffer;

public class Skin {

    public int id;
    public int count;
    public int[] transformationTypes;
    public int[][] skinList;

    public Skin() {
    }

    private Skin(ByteBuffer buffer) {
        this.count = buffer.get() & 255;
        this.transformationTypes = new int[this.count];
        this.skinList = new int[this.count][];

        int i;
        for(i = 0; i < this.count; ++i) {
            this.transformationTypes[i] = buffer.get() & 255;
        }

        for(i = 0; i < this.count; ++i) {
            this.skinList[i] = new int[buffer.get() & 255];
        }

        for(i = 0; i < this.count; ++i) {
            for(int j = 0; j < this.skinList[i].length; ++j) {
                this.skinList[i][j] = buffer.get() & 255;
            }
        }

    }

    public static Skin decode(ByteBuffer buffer) {
        Skin skin = new Skin();
        skin.count = buffer.get() & 255;
        skin.transformationTypes = new int[skin.count];
        skin.skinList = new int[skin.count][];

        int i;
        for(i = 0; i < skin.count; ++i) {
            skin.transformationTypes[i] = buffer.get() & 255;
        }

        for(i = 0; i < skin.count; ++i) {
            skin.skinList[i] = new int[buffer.get() & 255];
        }

        for(i = 0; i < skin.count; ++i) {
            for(int j = 0; j < skin.skinList[i].length; ++j) {
                skin.skinList[i][j] = buffer.get() & 255;
            }
        }

        return skin;
    }
}
