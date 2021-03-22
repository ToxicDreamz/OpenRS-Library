package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.objects.ObjectType;
import net.openrs.cache.type.objects.ObjectTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ObjectDefTo317Dumper {
    public ObjectDefTo317Dumper() {
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var3 = null;

        try {
            ObjectTypeList list = new ObjectTypeList();
            list.initialize(cache);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(dir, "loc.dat")));
            DataOutputStream idx = new DataOutputStream(new FileOutputStream(new File(dir, "loc.idx")));
            dos.writeShort(list.size());
            idx.writeShort(list.size());

            for(int i = 0; i < list.size(); ++i) {
                ObjectType def = list.list(i);
                int start = dos.size();
                if (def != null) {
                    def.encode(dos);
                }

                dos.writeByte(0);
                int end = dos.size();
                idx.writeShort(end - start);
                double var11 = (double)(i + 1) / (double)list.size() * 100.0D;
            }

            dos.close();
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
