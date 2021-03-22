package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.sequences.SequenceType;
import net.openrs.cache.type.sequences.SequenceTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class SeqTo317Converter {
    public SeqTo317Converter() {
    }

    public static void main(String[] args) throws Exception {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var3 = null;

        try {
            SequenceTypeList list = new SequenceTypeList();
            list.initialize(cache);
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(dir, "seq.dat")));
            dos.writeShort(list.size());

            for(int i = 0; i < list.size(); ++i) {
                SequenceType seq = list.list(i);
                if (seq != null) {
                    seq.encode(dos);
                }

                dos.writeByte(0);
                double var8 = (double)(i + 1) / (double)list.size() * 100.0D;
            }

            dos.close();
        } catch (Throwable var17) {
            var3 = var17;
            throw var17;
        } finally {
            if (cache != null) {
                if (var3 != null) {
                    try {
                        cache.close();
                    } catch (Throwable var16) {
                        var3.addSuppressed(var16);
                    }
                } else {
                    cache.close();
                }
            }

        }
    }
}
