package net.openrs.cache.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.openrs.cache.xtea.XteaKey;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;

public class XTEADumper {
    public XTEADumper() {
    }

    public static void main(String[] args) {
        String getUrl = "https://api.github.com/repos/runelite/runelite/tags";
        System.out.println(String.format("fetching latest runelite version: %s", "https://api.github.com/repos/runelite/runelite/tags"));

        try {
            HttpURLConnection connection = (HttpURLConnection)(new URL("https://api.github.com/repos/runelite/runelite/tags")).openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Throwable var6 = null;

                String xteaUrl;
                try {
                    while((xteaUrl = reader.readLine()) != null) {
                        buffer.append(xteaUrl);
                    }
                } catch (Throwable var69) {
                    var6 = var69;
                    throw var69;
                } finally {
                    if (reader != null) {
                        if (var6 != null) {
                            try {
                                reader.close();
                            } catch (Throwable var66) {
                                var6.addSuppressed(var66);
                            }
                        } else {
                            reader.close();
                        }
                    }

                }

                String json = buffer.toString();
                String versionTag = json.substring(json.indexOf("runelite-parent-") + 16, json.indexOf("\",\"")).trim();
                System.out.println(String.format("latest runelite version: %s", versionTag));
                xteaUrl = String.format("https://api.runelite.net/runelite-%s/xtea", versionTag);
                System.out.println(String.format("runelite xtea url: %s", xteaUrl));
                connection = (HttpURLConnection)(new URL(xteaUrl)).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-GB;     rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13 (.NET CLR 3.5.30729)");
                responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    new StringBuffer();
                    Gson gson = (new GsonBuilder()).create();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Throwable var10 = null;

                    try {
                        XteaKey[] xteas = gson.fromJson(bufferedReader, XteaKey[].class);
                        if (xteas != null) {
                            int count = 0;
                            XteaKey[] var13 = xteas;
                            int var14 = xteas.length;

                            for(int var15 = 0; var15 < var14; ++var15) {
                                XteaKey xtea = var13[var15];
                                PrintWriter writer = new PrintWriter(new FileWriter(Paths.get("repository", "xtea", "maps", xtea.getRegion() + ".txt").toFile()));
                                Throwable var18 = null;

                                try {
                                    System.out.println("writing xtea region: " + xtea.getRegion());
                                    int[] var19 = xtea.getKeys();
                                    int var20 = var19.length;

                                    for(int var21 = 0; var21 < var20; ++var21) {
                                        int key = var19[var21];
                                        writer.println(key);
                                    }

                                    ++count;
                                } catch (Throwable var67) {
                                    var18 = var67;
                                    throw var67;
                                } finally {
                                    if (writer != null) {
                                        if (var18 != null) {
                                            try {
                                                writer.close();
                                            } catch (Throwable var65) {
                                                var18.addSuppressed(var65);
                                            }
                                        } else {
                                            writer.close();
                                        }
                                    }

                                }
                            }

                            System.out.println(String.format("dumped: %d xteas", count));
                            return;
                        }
                    } catch (Throwable var71) {
                        var10 = var71;
                        throw var71;
                    } finally {
                        if (bufferedReader != null) {
                            if (var10 != null) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable var64) {
                                    var10.addSuppressed(var64);
                                }
                            } else {
                                bufferedReader.close();
                            }
                        }

                    }

                }
            }
        } catch (IOException var73) {
            var73.printStackTrace();
        }
    }
}
