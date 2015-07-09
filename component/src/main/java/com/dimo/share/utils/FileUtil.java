package com.dimo.share.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Alsor Zhou on 3/28/14.
 */
public class FileUtil {
    /**
     * Find the files under folder which matched the pattern
     *
     * @param dir    target folder
     * @param filter filename pattern
     * @return file list
     */
    public static List<String> listFiles(File dir, final String filter) {
        Log.i("Util", "list files under " + dir);

        List<String> files = new ArrayList<String>();

        File[] fs = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(filter);
            }
        });

        for (File f : fs) {
            files.add(f.getAbsolutePath());
        }

        return files;
    }

    /**
     * Create directory if not existed
     *
     * @param path destination path
     * @return success / fail
     */
    public static boolean createDirIfNotExists(String path) {
        boolean ret = true;

        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("TravellerLog :: ", "Problem creating folder : " + path);
                ret = false;
            }
        }
        return ret;
    }

    /**
     * Create file under data directory
     *
     * @param context
     * @param path
     * @return
     */
    public static File createDataFile(Context context, String path) {
        String installation = getDataDirectory(context);

        File file = new File(installation + "/" + path);

        return file;
    }

    public static String getDataDirectory(Context context) {
        File dataDir = context.getExternalFilesDir("");

//        PackageManager m = context.getPackageManager();
//        String s = context.getPackageName();
//        PackageInfo p = null;
//        try {
//            p = m.getPackageInfo(s, 0);
//            dataDir = p.applicationInfo.;
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        return dataDir.getAbsolutePath();
    }

    /**
     * Helper to retrieve the path of an image URI
     *
     * @param activity
     * @param uri
     * @return
     */
    public static String getPathFromUri(Activity activity, Uri uri) {
        // just some safety built in
        if (uri == null) {
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    /**
     * Copy folder under asset to destination
     *
     * @param assetManager
     * @param fromAssetPath
     * @param toPath
     * @return
     */
    public static boolean copyAssetFolder(AssetManager assetManager,
                                          String fromAssetPath, String toPath) {
        try {
            String[] files = assetManager.list(fromAssetPath);
            boolean result = new File(toPath).mkdirs();
            if (result) {
                Timber.i("Create path success " + toPath);
            } else {
                Timber.e("Create path failed " + toPath);
            }
            boolean res = true;
            for (String file : files)
                if (file.contains(".")) {
                    res &= copyAsset(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                } else {
                    res &= copyAssetFolder(assetManager,
                            fromAssetPath + "/" + file,
                            toPath + "/" + file);
                }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Copy asset
     *
     * @param assetManager
     * @param fromAssetPath
     * @param toPath
     * @return
     */
    private static boolean copyAsset(AssetManager assetManager,
                                     String fromAssetPath, String toPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Copy file stream
     *
     * @param in
     * @param out
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    /**
     * Copy file
     *
     * @param in  input file
     * @param out output file
     * @throws java.io.IOException
     */
    public static void copyFile(File in, File out) throws IOException {
        FileOutputStream outputStream;
        FileInputStream inputStream;

        try {
            outputStream = new FileOutputStream(out);
            inputStream = new FileInputStream(in);

            copyFile(inputStream, outputStream);

            outputStream.flush();

            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}
