package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.npcs.NpcType;
import net.openrs.cache.type.npcs.NpcTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NpcDefinitionTo317Dumper {

    public NpcDefinitionTo317Dumper() {
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var3 = null;

        try {
            NpcTypeList list = new NpcTypeList();
            list.initialize(cache);
            DataOutputStream dat = new DataOutputStream(new FileOutputStream(new File(dir, "npc.dat")));
            DataOutputStream idx = new DataOutputStream(new FileOutputStream(new File(dir, "npc.idx")));
            dat.writeShort(list.size());
            idx.writeShort(list.size());

            for(int i = 0; i < list.size(); ++i) {
                NpcType def = list.list(i);
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
