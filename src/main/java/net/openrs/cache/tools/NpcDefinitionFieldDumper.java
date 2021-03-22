package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.npcs.NpcType;
import net.openrs.cache.type.npcs.NpcTypeList;
import net.openrs.cache.util.ClassFieldPrinter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class NpcDefinitionFieldDumper {

    public NpcDefinitionFieldDumper() {
    }

    public static void main(String[] args) {
        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var2 = null;

            try {
                NpcTypeList list = new NpcTypeList();
                list.initialize(cache);
                ClassFieldPrinter printer = new ClassFieldPrinter();
                printer.setDefaultObject(new NpcType(0));

                try {
                    PrintWriter writer = new PrintWriter(new File("./dump/npcdef_fields.txt"));
                    Throwable var6 = null;

                    try {
                        for(int i = 0; i < list.size(); ++i) {
                            NpcType type = list.list(i);
                            if (type != null && type.getName() != null) {
                                printer.printFields(type);
                            }
                        }

                        writer.print(printer.getBuilder());
                    } catch (Throwable var34) {
                        var6 = var34;
                        throw var34;
                    } finally {
                        if (writer != null) {
                            if (var6 != null) {
                                try {
                                    writer.close();
                                } catch (Throwable var33) {
                                    var6.addSuppressed(var33);
                                }
                            } else {
                                writer.close();
                            }
                        }

                    }
                } catch (Exception var36) {
                    var36.printStackTrace();
                }
            } catch (Throwable var37) {
                var2 = var37;
                throw var37;
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
        } catch (IOException var39) {
            var39.printStackTrace();
        }

    }
}
