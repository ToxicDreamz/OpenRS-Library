package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.items.ItemType;
import net.openrs.cache.type.items.ItemTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ItemDefinitionTo317Dumper {

    public ItemDefinitionTo317Dumper() {
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var3 = null;

        try {
            ItemTypeList list = new ItemTypeList();
            list.initialize(cache);
            DataOutputStream dat = new DataOutputStream(new FileOutputStream(new File(dir, "obj.dat")));
            DataOutputStream idx = new DataOutputStream(new FileOutputStream(new File(dir, "obj.idx")));
            idx.writeShort(list.size());
            dat.writeShort(list.size());

            for(int i = 0; i < list.size(); ++i) {
                ItemType def = list.list(i);
                int start = dat.size();
                if (def != null) {
                    def.encode(dat);
                }

                dat.writeByte(0);
                int end = dat.size();
                idx.writeShort(end - start);
                double progress = (double)(i + 1) / (double)list.size() * 100.0D;
                System.out.println(String.format("%.2f%s", progress, "%"));
            }

            dat.close();
            idx.close();
        } catch (Throwable var20) {
            var3 = var20;
            throw var20;
        } finally {
            if (cache != null) {
                if (var3 != null) {
                    try {
                        cache.close();
                    } catch (Throwable var19) {
                        var3.addSuppressed(var19);
                    }
                } else {
                    cache.close();
                }
            }

        }
    }
}
