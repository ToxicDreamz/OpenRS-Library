package net.openrs.cache.tools;

import net.openrs.cache.*;
import net.openrs.cache.skeleton.Skeleton;
import net.openrs.cache.skeleton.Skin;
import net.openrs.cache.type.CacheIndex;
import net.openrs.cache.util.CompressionUtils;

import java.io.*;
import java.nio.ByteBuffer;

public class AnimationTo317Dumper {

    public static boolean headerPacked;

    public AnimationTo317Dumper() {
    }

    public static void main(String[] args) throws Exception {
        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var2 = null;

        try {
            File dir = new File("./dump/anims/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            ReferenceTable skeletonTable = cache.getReferenceTable(CacheIndex.SKELETONS);
            Skeleton[][] skeletons = new Skeleton[skeletonTable.capacity()][];

            for(int mainSkeletonId = 0; mainSkeletonId < skeletonTable.capacity(); ++mainSkeletonId) {
                if (skeletonTable.getEntry(mainSkeletonId) != null) {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    DataOutputStream dat = new DataOutputStream(bos);
                    Throwable var9 = null;

                    try {
                        Archive skeletonArchive = Archive.decode(cache.read(CacheIndex.SKELETONS, mainSkeletonId).getData(), skeletonTable.getEntry(mainSkeletonId).size());
                        int subSkeletonCount = skeletonArchive.size();
                        headerPacked = false;
                        skeletons[mainSkeletonId] = new Skeleton[subSkeletonCount];

                        for(int subSkeletonId = 0; subSkeletonId < subSkeletonCount; ++subSkeletonId) {
                            readNext(cache, dat, skeletonArchive, mainSkeletonId, subSkeletonId, skeletons);
                        }
                    } catch (Throwable var57) {
                        var9 = var57;
                        throw var57;
                    } finally {
                        if (dat != null) {
                            if (var9 != null) {
                                try {
                                    dat.close();
                                } catch (Throwable var54) {
                                    var9.addSuppressed(var54);
                                }
                            } else {
                                dat.close();
                            }
                        }

                    }

                    FileOutputStream fos = new FileOutputStream(new File(dir, mainSkeletonId + ".gz"));
                    var9 = null;

                    try {
                        fos.write(CompressionUtils.gzip(bos.toByteArray()));
                    } catch (Throwable var55) {
                        var9 = var55;
                        throw var55;
                    } finally {
                        if (fos != null) {
                            if (var9 != null) {
                                try {
                                    fos.close();
                                } catch (Throwable var53) {
                                    var9.addSuppressed(var53);
                                }
                            } else {
                                fos.close();
                            }
                        }

                    }

                    double progress = (double)(mainSkeletonId + 1) / (double)skeletonTable.capacity() * 100.0D;
                    System.out.println(String.format("%.2f%s", progress, "%"));
                }
            }

            System.out.println(String.format("Dumped %d skeletons.", skeletonTable.capacity()));
        } catch (Throwable var59) {
            var2 = var59;
            throw var59;
        } finally {
            if (cache != null) {
                if (var2 != null) {
                    try {
                        cache.close();
                    } catch (Throwable var52) {
                        var2.addSuppressed(var52);
                    }
                } else {
                    cache.close();
                }
            }

        }
    }

    public static void readNext(Cache cache, DataOutputStream dos, Archive archive, int mainSkeletonId, int subSkeletonId, Skeleton[][] skeletons) throws IOException {
        ByteBuffer skeletonBuffer = archive.getEntry(subSkeletonId);
        if (skeletonBuffer.remaining() != 0) {
            int skinId = (skeletonBuffer.array()[0] & 255) << 8 | skeletonBuffer.array()[1] & 255;
            Container skinContainer = cache.read(CacheIndex.SKINS, skinId);
            ByteBuffer skinBuffer = skinContainer.getData();
            if (skinBuffer != null) {
                Skin skin = Skin.decode(skinBuffer);
                if (!headerPacked) {
                    dos.writeShort(skin.count);

                    int i;
                    for(i = 0; i < skin.count; ++i) {
                        dos.writeShort(skin.transformationTypes[i]);
                    }

                    for(i = 0; i < skin.count; ++i) {
                        dos.writeShort(skin.skinList[i].length);
                    }

                    for(i = 0; i < skin.count; ++i) {
                        for(int j = 0; j < skin.skinList[i].length; ++j) {
                            dos.writeShort(skin.skinList[i][j]);
                        }
                    }

                    dos.writeShort(archive.size());
                    headerPacked = true;
                }

                dos.writeShort(subSkeletonId);
                skeletons[mainSkeletonId][subSkeletonId] = Skeleton.decode(skeletonBuffer, skin, dos);
            }
        }
    }
}
