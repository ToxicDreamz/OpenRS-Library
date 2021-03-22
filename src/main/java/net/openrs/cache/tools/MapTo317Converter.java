package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.Container;
import net.openrs.cache.FileStore;
import net.openrs.cache.util.CompressionUtils;
import net.openrs.cache.util.XTEAManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Paths;

public class MapTo317Converter {

    private static final int MAX_REGION = 32768;
    private static final int MAP_SCALE = 2;
    private static final boolean LABEL = true;
    private static final boolean OUTLINE = true;
    private static final boolean FILL = true;

    public MapTo317Converter() {
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File mapDir = new File("./dump/index4/");
        if (!mapDir.exists()) {
            mapDir.mkdirs();
        }

        if (XTEAManager.load(Paths.get("repository", "xteas").toFile())) {
            try {
                Cache cache = new Cache(FileStore.open("./repository/cache/"));
                Throwable var4 = null;

                try {
                    RandomAccessFile raf = new RandomAccessFile((new File(dir, "map_index")).toPath().toString(), "rw");
                    Throwable var6 = null;

                    try {
                        System.out.println("Generating map_index...");
                        int total = 0;
                        raf.seek(2L);
                        int mapCount = 0;
                        int landCount = 0;

                        int end;
                        int i;
                        int x;
                        int y;
                        for(end = 0; end < 256; ++end) {
                            for(i = 0; i < 256; ++i) {
                                int var17 = end << 8 | i;
                                x = cache.getFileId(5, "m" + end + "_" + i);
                                y = cache.getFileId(5, "l" + end + "_" + i);
                                if (x != -1 && y != -1) {
                                    raf.writeShort(var17);
                                    raf.writeShort(x);
                                    raf.writeShort(y);
                                    ++total;
                                }
                            }
                        }

                        end = (int)raf.getFilePointer();
                        raf.seek(0L);
                        raf.writeShort(total);
                        raf.seek((long)end);
                        raf.close();
                        System.out.println("Done dumping map_index.");

                        for(i = 0; i < 32768; ++i) {
                            int[] keys = XTEAManager.lookup(i);
                            x = i >> 8;
                            y = i & 255;
                            int map = cache.getFileId(5, "m" + x + "_" + y);
                            int land = cache.getFileId(5, "l" + x + "_" + y);
                            Container container;
                            byte[] data;
                            FileOutputStream fos;
                            Throwable var20;
                            if (map != -1) {
                                try {
                                    container = cache.read(5, map);
                                    data = new byte[container.getData().limit()];
                                    container.getData().get(data);
                                    fos = new FileOutputStream(new File(mapDir, map + ".gz"));
                                    var20 = null;

                                    try {
                                        fos.write(CompressionUtils.gzip(data));
                                    } catch (Throwable var105) {
                                        var20 = var105;
                                        throw var105;
                                    } finally {
                                        if (fos != null) {
                                            if (var20 != null) {
                                                try {
                                                    fos.close();
                                                } catch (Throwable var104) {
                                                    var20.addSuppressed(var104);
                                                }
                                            } else {
                                                fos.close();
                                            }
                                        }

                                    }

                                    ++mapCount;
                                } catch (Exception var109) {
                                    System.out.println(String.format("Failed to decrypt map: %d", map));
                                }
                            }

                            if (land != -1) {
                                try {
                                    container = cache.read(5, land, keys);
                                    data = new byte[container.getData().limit()];
                                    container.getData().get(data);
                                    fos = new FileOutputStream(new File(mapDir, land + ".gz"));
                                    var20 = null;

                                    try {
                                        fos.write(CompressionUtils.gzip(data));
                                    } catch (Throwable var103) {
                                        var20 = var103;
                                        throw var103;
                                    } finally {
                                        if (fos != null) {
                                            if (var20 != null) {
                                                try {
                                                    fos.close();
                                                } catch (Throwable var102) {
                                                    var20.addSuppressed(var102);
                                                }
                                            } else {
                                                fos.close();
                                            }
                                        }

                                    }

                                    ++landCount;
                                } catch (Exception var107) {
                                    System.out.println(String.format("Failed to decrypt landscape: %d", land));
                                }
                            }

                            double progress = (double)(i + 1) / 32768.0D * 100.0D;
                            System.out.println(String.format("%.2f%s", progress, "%"));
                        }

                        i = mapCount + landCount;
                        System.out.println(String.format("Dumped %d map count %d land count %d total count", mapCount, landCount, i));
                    } catch (Throwable var110) {
                        var6 = var110;
                        throw var110;
                    } finally {
                        if (raf != null) {
                            if (var6 != null) {
                                try {
                                    raf.close();
                                } catch (Throwable var101) {
                                    var6.addSuppressed(var101);
                                }
                            } else {
                                raf.close();
                            }
                        }

                    }
                } catch (Throwable var112) {
                    var4 = var112;
                    throw var112;
                } finally {
                    if (cache != null) {
                        if (var4 != null) {
                            try {
                                cache.close();
                            } catch (Throwable var100) {
                                var4.addSuppressed(var100);
                            }
                        } else {
                            cache.close();
                        }
                    }

                }
            } catch (IOException var114) {
                var114.printStackTrace();
            }

        }
    }
}
