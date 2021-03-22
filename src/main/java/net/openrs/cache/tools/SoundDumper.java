package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.ReferenceTable;
import net.openrs.cache.sound.SoundEffect;
import net.openrs.cache.type.CacheIndex;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.nio.file.Paths;

public class SoundDumper {

    public SoundDumper() {
    }

    public static void main(String[] args) {
        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var2 = null;

            try {
                File dir = Paths.get("sounds").toFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                export(cache, dir, true);
            } catch (Throwable var12) {
                var2 = var12;
                throw var12;
            } finally {
                if (cache != null) {
                    if (var2 != null) {
                        try {
                            cache.close();
                        } catch (Throwable var11) {
                            var2.addSuppressed(var11);
                        }
                    } else {
                        cache.close();
                    }
                }

            }
        } catch (IOException var14) {
            var14.printStackTrace();
        }

    }

    private static void export(Cache cache, File dir, boolean wav) {
        ReferenceTable table = cache.getReferenceTable(CacheIndex.SOUND_EFFECT);

        for(int i = 0; i < table.capacity(); ++i) {
            try {
                SoundEffect effect = SoundEffect.decode(cache, i);
                byte[] data = effect.mix();
                if (wav) {
                    AudioFormat audioFormat = new AudioFormat(22050.0F, 8, 1, true, false);
                    AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(data), audioFormat, data.length);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    AudioSystem.write(ais, AudioFileFormat.Type.WAVE, bos);
                    data = bos.toByteArray();
                }

                FileOutputStream fos = new FileOutputStream(new File(dir, i + (wav ? ".wav" : ".dat")));
                Throwable var22 = null;

                try {
                    fos.write(data);
                } catch (Throwable var18) {
                    var22 = var18;
                    throw var18;
                } finally {
                    if (fos != null) {
                        if (var22 != null) {
                            try {
                                fos.close();
                            } catch (Throwable var17) {
                                var22.addSuppressed(var17);
                            }
                        } else {
                            fos.close();
                        }
                    }

                }
            } catch (Exception var20) {
                System.out.println("error decoding sound: " + i);
                continue;
            }

            System.out.println("decoding sound: " + i);
        }

        System.out.println("decoded: " + table.capacity() + " sounds");
    }
}
