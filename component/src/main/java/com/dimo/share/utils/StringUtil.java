package com.dimo.share.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alsor Zhou on 13-11-9.
 */
public class StringUtil {
    /**
     * Generate random string with specific length
     *
     * http://stackoverflow.com/a/12116194
     *
     * @param len random string length
     * @return random string
     */
    public static String randomString(int len) {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(len);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    /**
     * Generate 8 character string as filename
     *
     * @return filename
     */
    public static String randomFilename() {
        return randomString(8);
    }

    /**
     * http://stackoverflow.com/a/3414749
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getRealPathFromString(String url) {
        return url.substring(7, url.length());
    }

    public static boolean stringMatchedWithRegex(String s, String pattern) {
        try {
            Pattern patt = Pattern.compile(pattern);
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
    }
}
