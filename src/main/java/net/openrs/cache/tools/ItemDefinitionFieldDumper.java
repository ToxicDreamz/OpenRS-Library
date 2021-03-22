package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.items.ItemType;
import net.openrs.cache.type.items.ItemTypeList;
import net.openrs.cache.util.ClassFieldPrinter;

import java.io.File;
import java.io.PrintWriter;

public class ItemDefinitionFieldDumper {

    public ItemDefinitionFieldDumper() {
    }

    public static void main(String[] args) {
        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var2 = null;

            try {
                ItemTypeList list = new ItemTypeList();
                list.initialize(cache);
                PrintWriter writer = new PrintWriter(new File("./dump/itemdef_fields_txt"));
                Throwable var5 = null;

                try {
                    ClassFieldPrinter printer = new ClassFieldPrinter();

                    for(int i = 0; i < list.size(); ++i) {
                        ItemType type = list.list(i);
                        if (type != null && type.getName() != null) {
                            try {
                                printer.setDefaultObject(new ItemType(i));
                                printer.printFields(type, "def.");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    writer.write(printer.getBuilder().toString());
                } catch (Throwable var36) {
                    var5 = var36;
                    throw var36;
                } finally {
                    if (writer != null) {
                        if (var5 != null) {
                            try {
                                writer.close();
                            } catch (Throwable var34) {
                                var5.addSuppressed(var34);
                            }
                        } else {
                            writer.close();
                        }
                    }

                }
            } catch (Throwable var38) {
                var2 = var38;
                throw var38;
            } finally {
                if (cache != null) {
                    if (var2 != null) {
                        try {
                            cache.close();
                        } catch (Throwable var33) {
                            var2.addSuppressed(var33);
                        }
                    } else {
                        cache.close();
                    }
                }

            }
        } catch (Exception var40) {
            var40.printStackTrace();
        }

    }
}
