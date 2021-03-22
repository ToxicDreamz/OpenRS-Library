package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.spotanims.SpotAnimType;
import net.openrs.cache.type.spotanims.SpotAnimTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GraphicsTo317Dumper {

    public GraphicsTo317Dumper() {
    }

    public static void main(String[] args) {
        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var2 = null;

            try {
                File dir = new File("./dump/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                SpotAnimTypeList list = new SpotAnimTypeList();
                list.initialize(cache);
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(dir, "spotanim.dat")));
                Throwable var6 = null;

                try {
                    dos.writeShort(list.size());

                    for(int i = 0; i < list.size(); ++i) {
                        SpotAnimType spotAnim = list.list(i);
                        if (spotAnim == null) {
                            spotAnim = new SpotAnimType(1);
                        }

                        spotAnim.encode(dos);
                        double progress = (double)(i + 1) / (double)list.size() * 100.0D;
                        System.out.println(String.format("%.2f%s", progress, "%"));
                    }
                } catch (Throwable var34) {
                    var6 = var34;
                    throw var34;
                } finally {
                    if (dos != null) {
                        if (var6 != null) {
                            try {
                                dos.close();
                            } catch (Throwable var33) {
                                var6.addSuppressed(var33);
                            }
                        } else {
                            dos.close();
                        }
                    }

                }
            } catch (Throwable var36) {
                var2 = var36;
                throw var36;
            } finally {
                if (cache != null) {
                    if (var2 != null) {
                        try {
                            cache.close();
                        } catch (Throwable var32) {
                            var2.addSuppressed(var32);
                        }
                    } else {
                        cache.close();
                    }
                }

            }
        } catch (IOException var38) {
            var38.printStackTrace();
        }

    }
}
