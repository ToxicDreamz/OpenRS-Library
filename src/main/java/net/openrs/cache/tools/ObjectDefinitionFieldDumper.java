package net.openrs.cache.tools;

import net.openrs.cache.Cache;
import net.openrs.cache.FileStore;
import net.openrs.cache.type.objects.ObjectType;
import net.openrs.cache.type.objects.ObjectTypeList;
import net.openrs.cache.util.ClassFieldPrinter;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ObjectDefinitionFieldDumper {

    public ObjectDefinitionFieldDumper() {
    }

    public static void main(String[] args) {
        try {
            Cache cache = new Cache(FileStore.open("./repository/cache/"));
            Throwable var2 = null;

            try {
                ObjectTypeList list = new ObjectTypeList();
                list.initialize(cache);
                File dir = new File("./dump/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                PrintWriter writer = new PrintWriter(new File(dir, "obj_fields.txt"));
                Throwable var6 = null;

                try {
                    for(int i = 0; i < list.size(); ++i) {
                        ObjectType type = list.list(i);
                        if (type != null && type.getName() != null) {
                            try {
                                ClassFieldPrinter printer = new ClassFieldPrinter();
                                printer.setDefaultObject(new ObjectType(i));
                                printer.printFields(type);
                                writer.print(printer.getBuilder().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Throwable var36) {
                    var6 = var36;
                    throw var36;
                } finally {
                    if (writer != null) {
                        if (var6 != null) {
                            try {
                                writer.close();
                            } catch (Throwable var34) {
                                var6.addSuppressed(var34);
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
        } catch (IOException var40) {
            var40.printStackTrace();
        }

    }
}
