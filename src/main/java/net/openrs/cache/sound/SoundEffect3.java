package net.openrs.cache.sound;

import java.nio.ByteBuffer;

public class SoundEffect3 {
    static float[][] minimisedCoefficients = new float[2][8];
    static int[][] coefficients = new int[2][8];
    static float fowardMinimisedCoefficientMultiplier;
    static int fowardMultiplier;
    int[] pairs = new int[2];
    int[][][] phases = new int[2][2][4];
    int[][][] magnitudes = new int[2][2][4];
    int[] unity = new int[2];

    SoundEffect3() {
    }

    float interpolateMagniture(int var1, int var2, float var3) {
        float var4 = (float) this.magnitudes[var1][0][var2] + var3 * (float) (this.magnitudes[var1][1][var2] - this.magnitudes[var1][0][var2]);
        var4 *= 0.0015258789F;
        return 1.0F - (float) Math.pow(10.0D, (double) (-var4 / 20.0F));
    }

    float interpolatePhase(int var1, int var2, float var3) {
        float var4 = (float) this.phases[var1][0][var2] + var3 * (float) (this.phases[var1][1][var2] - this.phases[var1][0][var2]);
        var4 *= 1.2207031E-4F;
        return normalise(var4);
    }

    int compute(int var1, float var2) {
        float var3;
        if (var1 == 0) {
            var3 = (float) this.unity[0] + (float) (this.unity[1] - this.unity[0]) * var2;
            var3 *= 0.0030517578F;
            fowardMinimisedCoefficientMultiplier = (float) Math.pow(0.1D, (double) (var3 / 20.0F));
            fowardMultiplier = (int) (fowardMinimisedCoefficientMultiplier * 65536.0F);
        }

        if (this.pairs[var1] == 0) {
            return 0;
        } else {
            var3 = this.interpolateMagniture(var1, 0, var2);
            minimisedCoefficients[var1][0] = -2.0F * var3 * (float) Math.cos((double) this.interpolatePhase(var1, 0, var2));
            minimisedCoefficients[var1][1] = var3 * var3;

            float[] var10000;
            int var4;
            for (var4 = 1; var4 < this.pairs[var1]; ++var4) {
                var3 = this.interpolateMagniture(var1, var4, var2);
                float var5 = -2.0F * var3 * (float) Math.cos((double) this.interpolatePhase(var1, var4, var2));
                float var6 = var3 * var3;
                minimisedCoefficients[var1][var4 * 2 + 1] = minimisedCoefficients[var1][var4 * 2 - 1] * var6;
                minimisedCoefficients[var1][var4 * 2] = minimisedCoefficients[var1][var4 * 2 - 1] * var5 + minimisedCoefficients[var1][var4 * 2 - 2] * var6;

                for (int var7 = var4 * 2 - 1; var7 >= 2; --var7) {
                    var10000 = minimisedCoefficients[var1];
                    var10000[var7] += minimisedCoefficients[var1][var7 - 1] * var5 + minimisedCoefficients[var1][var7 - 2] * var6;
                }

                var10000 = minimisedCoefficients[var1];
                var10000[1] += minimisedCoefficients[var1][0] * var5 + var6;
                var10000 = minimisedCoefficients[var1];
                var10000[0] += var5;
            }

            if (var1 == 0) {
                for (var4 = 0; var4 < this.pairs[0] * 2; ++var4) {
                    var10000 = minimisedCoefficients[0];
                    var10000[var4] *= fowardMinimisedCoefficientMultiplier;
                }
            }

            for (var4 = 0; var4 < this.pairs[var1] * 2; ++var4) {
                coefficients[var1][var4] = (int) (minimisedCoefficients[var1][var4] * 65536.0F);
            }

            return this.pairs[var1] * 2;
        }
    }

    final void decode(ByteBuffer var1, AudioEnvelope var2) {
        int var3 = var1.get() & 255;
        this.pairs[0] = var3 >> 4;
        this.pairs[1] = var3 & 15;
        if (var3 != 0) {
            this.unity[0] = var1.getShort() & '\uffff';
            this.unity[1] = var1.getShort() & '\uffff';
            int var4 = var1.get() & 255;

            int var5;
            int var6;
            for (var5 = 0; var5 < 2; ++var5) {
                for (var6 = 0; var6 < this.pairs[var5]; ++var6) {
                    this.phases[var5][0][var6] = var1.getShort() & '\uffff';
                    this.magnitudes[var5][0][var6] = var1.getShort() & '\uffff';
                }
            }

            for (var5 = 0; var5 < 2; ++var5) {
                for (var6 = 0; var6 < this.pairs[var5]; ++var6) {
                    if ((var4 & 1 << var5 * 4 << var6) != 0) {
                        this.phases[var5][1][var6] = var1.getShort() & '\uffff';
                        this.magnitudes[var5][1][var6] = var1.getShort() & '\uffff';
                    } else {
                        this.phases[var5][1][var6] = this.phases[var5][0][var6];
                        this.magnitudes[var5][1][var6] = this.magnitudes[var5][0][var6];
                    }
                }
            }

            if (var4 != 0 || this.unity[1] != this.unity[0]) {
                var2.decodeSegments(var1);
            }
        } else {
            int[] var7 = this.unity;
            this.unity[1] = 0;
            var7[0] = 0;
        }

    }

    static float normalise(float var0) {
        float var1 = 32.703197F * (float) Math.pow(2.0D, (double) var0);
        return var1 * 3.1415927F / 11025.0F;
    }
}
