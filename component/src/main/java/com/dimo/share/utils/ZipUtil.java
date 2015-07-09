package com.dimo.share.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import timber.log.Timber;

/**
 * Created by Alsor Zhou on 15/4/18.
 */
public class ZipUtil {
    /**
     * Unzip the .zip file
     *
     * @param path              zip file path
     * @param destinationFolder target folder
     * @return success / fail
     */
    public static boolean unzip(String path, String destinationFolder) {
        try {
            FileInputStream is = new FileInputStream(path);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));
            ZipEntry ze = null;
            byte[] buffer = new byte[1024];
            int count;
            while ((ze = zis.getNextEntry()) != null) {
                Timber.v("Unzipping " + ze.getName());

                ///code to search is given string exists or not in a Sentence
                String haystack = ze.getName();
                String needle1 = "_MACOSX";
                int index1 = haystack.indexOf(needle1);
                if (index1 != -1) {
                    Timber.v("The string contains the substring "
                            + needle1);
                    continue;
                }

                if (ze.isDirectory()) {
                    _dirChecker(destinationFolder, ze.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(destinationFolder + "/" + ze.getName());
                    // replace for loop with:

                    while ((count = zis.read(buffer)) != -1) {
                        fout.write(buffer, 0, count);
                    }

                    fout.close();
                    zis.closeEntry();
                }


            }
            zis.close();
        } catch (Exception e) {
            e.printStackTrace();

            Timber.e("unzip", e);
            return false;
        }

        return true;
    }

    private static void _dirChecker(String destinationFolder, String subDir) {
        File f = new File(destinationFolder, subDir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}
