/*
 * Decompiled with CFR 0_87.
 * 
 * Could not load the following classes:
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.io.StringWriter
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.security.MessageDigest
 *  java.security.NoSuchAlgorithmException
 */
// немного текста
package ru.supervital.bestrates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {
    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public static String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream == null) return "";
        StringWriter stringWriter = new StringWriter();
        char[] arrc = new char[1024];
        try {
            int n;
            BufferedReader bufferedReader = new BufferedReader((Reader)new InputStreamReader(inputStream, "UTF-8"), 1024);
            while ((n = bufferedReader.read(arrc)) != -1) {
                stringWriter.write(arrc, 0, n);
            }
            return stringWriter.toString();
        }
        finally {
            inputStream.close();
        }
    }

    public static boolean isNull(String string) {
        if (!(string == null || string.length() <= 0 || string.equalsIgnoreCase("null") || string.equalsIgnoreCase("{null}") || string.equalsIgnoreCase("(null)"))) {
            return false;
        }
        return true;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public static final String md5(String string) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance((String)"MD5");
            messageDigest.update(string.getBytes());
            byte[] arrby = messageDigest.digest();
            StringBuilder stringBuilder = new StringBuilder();
            int n = arrby.length;
            int n2 = 0;
            block3 : do {
                if (n2 >= n) {
                    return stringBuilder.toString();
                }
                String string2 = Integer.toHexString((int)(255 & arrby[n2]));
                do {
                    String string3;
                    if (string2.length() >= 2) {
                        stringBuilder.append(string2);
                        ++n2;
                        continue block3;
                    }
                    string2 = string3 = "0" + string2;
                } while (true);
//OVV                break;
            } while (true);
        }
        catch (NoSuchAlgorithmException var1_8) {
            var1_8.printStackTrace();
            return "";
        }
    }
}

