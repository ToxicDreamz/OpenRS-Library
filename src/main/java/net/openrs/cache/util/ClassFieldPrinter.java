package net.openrs.cache.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class ClassFieldPrinter {

    private Map<String, Object> defaultFields = new HashMap();
    private StringBuilder builder = new StringBuilder();

    public ClassFieldPrinter() {
    }

    public void setDefaultObject(Object obj) {
        this.defaultFields.clear();

        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            Field[] var3 = fields;
            int var4 = fields.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Field field = var3[var5];
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    String name = field.getName();
                    Object value = field.get(obj);
                    if (value != null) {
                        this.defaultFields.put(name, value);
                    }
                }
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

    }

    public void printFields(Object obj) throws Exception {
        this.printFields(obj, "", false);
    }

    public void printFields(Object obj, String prefix) throws Exception {
        this.printFields(obj, prefix, false);
    }

    public void printFields(Object obj, String prefix, boolean printDefaults) throws Exception {
        Field[] fields = obj.getClass().getDeclaredFields();
        Map<String, Object> map = new TreeMap();
        Field[] var6 = fields;
        int var7 = fields.length;

        String text;
        for(int var8 = 0; var8 < var7; ++var8) {
            Field field = var6[var8];
            if (!Modifier.isStatic(field.getModifiers())) {
                field.setAccessible(true);
                text = field.getName();
                Object value = field.get(obj);
                if (value != null) {
                    map.put(text, value);
                }
            }
        }

        if (!map.isEmpty()) {
            this.builder.append(String.format("// %s\n", map.getOrDefault("name", "null")));
            this.builder.append(String.format("case %d:\n", (Integer)map.getOrDefault("id", -1)));
        }

        Iterator var12 = map.entrySet().iterator();

        while(true) {
            String key;
            String text2;
            do {
                Object value;
                do {
                    do {
                        if (!var12.hasNext()) {
                            this.builder.append("break;\n");
                            this.builder.append("\n");
                            return;
                        }

                        Map.Entry<String, Object> set = (Map.Entry)var12.next();
                        key = (String)set.getKey();
                        value = set.getValue();
                    } while(key.equalsIgnoreCase("id"));
                } while(key.equalsIgnoreCase("name"));

                text = convert(value);
                if (printDefaults) {
                    break;
                }

                text2 = convert(this.defaultFields.get(key));
            } while(text.equals(text2));

            if (prefix.isEmpty()) {
                this.builder.append(String.format("\t%s = %s\n", key, text));
            } else {
                this.builder.append(String.format("\t%s%s = %s\n", prefix, key, text));
            }
        }
    }

    private static String convert(Object value) {
        if (value == null) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            int i;
            if (value instanceof String[]) {
                builder.append("new String[] {");
                String[] array = (String[])((String[])value);

                for(i = 0; i < array.length; ++i) {
                    String s = array[i];
                    builder.append("\"").append(s).append("\"");
                    if (i < array.length - 1) {
                        builder.append(", ");
                    }
                }

                builder.append("};");
            } else if (value instanceof short[]) {
                builder.append("new short[] {");
                short[] array = (short[])((short[])value);

                for(i = 0; i < array.length; ++i) {
                    short s = array[i];
                    builder.append(s);
                    if (i < array.length - 1) {
                        builder.append(",");
                    }
                }

                builder.append("};");
            } else if (value instanceof int[]) {
                builder.append("new int[] {");
                int[] array = (int[])((int[])value);

                for(i = 0; i < array.length; ++i) {
                    int s = array[i];
                    builder.append(s);
                    if (i < array.length - 1) {
                        builder.append(",");
                    }
                }

                builder.append("};");
            } else {
                builder.append(value.toString()).append(";");
            }

            return builder.toString();
        }
    }

    public StringBuilder getBuilder() {
        return this.builder;
    }
}
