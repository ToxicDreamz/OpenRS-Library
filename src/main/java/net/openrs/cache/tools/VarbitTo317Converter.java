package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.varbits.VarBitType;
import net.openrs.cache.type.varbits.VarBitTypeList;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VarbitTo317Converter {

    public VarbitTo317Converter() {
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("./dump/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Cache cache = new Cache(FileStore.open("./repository/cache/"));
        Throwable var3 = null;

        try {
            VarBitTypeList list = new VarBitTypeList();
            list.initialize(cache);
            DataOutputStream dat = new DataOutputStream(new FileOutputStream(new File(dir, "varbit.dat")));
            dat.writeShort(list.size());

            for(int i = 0; i < list.size(); ++i) {
                VarBitType def = list.list(i);
                if (def != null) {
                    def.encode(dat);
                } else {
                    dat.writeByte(0);
                }

                System.out.println(i + " " + dat.size());
                double progress = (double)(i + 1) / (double)list.size() * 100.0D;
                System.out.println(String.format("%.2f%s", progress, "%"));
            }

            dat.close();
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
