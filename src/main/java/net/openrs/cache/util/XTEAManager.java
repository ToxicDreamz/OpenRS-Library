/**
* Copyright (c) Kyle Fricilone
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.openrs.cache.util;

import com.google.gson.Gson;
import net.openrs.cache.Constants;
import net.openrs.cache.xtea.XteaKey;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kyle Friz
 * 
 * @since Jun 30, 2015
 */
public class XTEAManager {

      private static final Map<Integer, int[]> maps = new HashMap();
      private static final Map<Integer, int[]> tables = new HashMap();
      private static final Gson gson = new Gson();
      public static final int[] NULL_KEYS = new int[4];


      public static final int[] lookupTable(int id) {
            int[] keys = tables.get(id);
            if (keys == null)
                  return NULL_KEYS;

            return keys;
      }

      public static final int[] lookupMap(int id) {
            int[] keys = maps.get(id);
            if (keys == null)
                  return NULL_KEYS;

            return keys;
      }

      public static int[] lookup(int region) {
            return maps.getOrDefault(region, NULL_KEYS);
      }

      public static boolean load(File xteaDir) throws IOException {
            int region;
            if (xteaDir.isDirectory()) {
                  File[] files = xteaDir.listFiles() == null ? new File[0] : xteaDir.listFiles();
                  File[] var2 = files;
                  int var3 = files.length;

                  for(int var4 = 0; var4 < var3; ++var4) {
                        File file = var2[var4];
                        if (file.getName().endsWith(".txt")) {
                              region = Integer.valueOf(file.getName().substring(0, file.getName().indexOf(".txt")));
                              int[] keys = Files.lines(xteaDir.toPath().resolve(file.getName())).map(Integer::valueOf).mapToInt(Integer::intValue).toArray();
                              maps.put(region, keys);
                        }
                  }
            } else {
                  FileReader fos = new FileReader(xteaDir);
                  Throwable var18 = null;

                  try {
                        XteaKey[] keys = (XteaKey[])gson.fromJson(fos, XteaKey[].class);
                        XteaKey[] var20 = keys;
                        int var21 = keys.length;

                        for(region = 0; region < var21; ++region) {
                              XteaKey key = var20[region];
                              maps.put(key.getRegion(), key.getKeys());
                        }
                  } catch (Throwable var15) {
                        var18 = var15;
                        throw var15;
                  } finally {
                        if (fos != null) {
                              if (var18 != null) {
                                    try {
                                          fos.close();
                                    } catch (Throwable var14) {
                                          var18.addSuppressed(var14);
                                    }
                              } else {
                                    fos.close();
                              }
                        }

                  }
            }

            return !maps.isEmpty();
      }
      
      public static void touch() { };
      
}
