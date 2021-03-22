package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.overlays.OverlayType;
import net.openrs.cache.type.overlays.OverlayTypeList;
import net.openrs.cache.type.underlays.UnderlayType;
import net.openrs.cache.type.underlays.UnderlayTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FloEncoder {
    public FloEncoder() {
    }

    public static void main(String[] args) {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var3 = null;

            try {
                UnderlayTypeList underlays = new UnderlayTypeList();
                OverlayTypeList overlays = new OverlayTypeList();
                underlays.initialize(cache);
                overlays.initialize(cache);
                int totalCount = underlays.size() + overlays.size();
                DataOutputStream dat = new DataOutputStream(new FileOutputStream(new File(dir, "flo.dat")));
                Throwable var8 = null;

                try {
                    dat.writeShort(underlays.size());
                    int count = 0;
                    System.out.println(String.format("encoding %d underlays", underlays.size()));

                    int i;
                    double progress;
                    for(i = 0; i < underlays.size(); ++i) {
                        UnderlayType underlay = underlays.list(i);
                        if (underlay != null) {
                            underlay.encode(dat);
                        } else {
                            dat.writeByte(0);
                        }

                        progress = (double)(count + 1) / (double)totalCount * 100.0D;
                        System.out.println(String.format("%.2f%s", progress, "%"));
                        ++count;
                    }

                    dat.writeShort(overlays.size());
                    System.out.println(String.format("encoding %d overlays", overlays.size()));

                    for(i = 0; i < overlays.size(); ++i) {
                        OverlayType overlay = overlays.list(i);
                        if (overlay != null) {
                            overlay.encode(dat);
                        } else {
                            dat.writeByte(0);
                        }

                        progress = (double)(count + 1) / (double)totalCount * 100.0D;
                        System.out.println(String.format("%.2f%s", progress, "%"));
                        ++count;
                    }

                    System.out.println(String.format("Dumped %d underlays and %d overlays.", underlays.size(), overlays.size()));
                } catch (Throwable var37) {
                    var8 = var37;
                    throw var37;
                } finally {
                    if (dat != null) {
                        if (var8 != null) {
                            try {
                                dat.close();
                            } catch (Throwable var36) {
                                var8.addSuppressed(var36);
                            }
                        } else {
                            dat.close();
                        }
                    }

                }
            } catch (Throwable var39) {
                var3 = var39;
                throw var39;
            } finally {
                if (cache != null) {
                    if (var3 != null) {
                        try {
                            cache.close();
                        } catch (Throwable var35) {
                            var3.addSuppressed(var35);
                        }
                    } else {
                        cache.close();
                    }
                }

            }
        } catch (IOException var41) {
            var41.printStackTrace();
        }

    }
}
