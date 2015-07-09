package com.dimo.share.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

import timber.log.Timber;

public class ImageUtil {
    
    private static final String TAG = "ImageUtil";
     
    public static Bitmap resizeBitmapToDisplaySize(Activity activity, Bitmap src){
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight(); 
        Timber.d(TAG, "元srcWidth = " + String.valueOf(srcWidth)
                + " px, 元srcHeight = " + String.valueOf(srcHeight) + " px");
 
        // 画面サイズを取得する
        Matrix matrix = new Matrix();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float screenWidth = metrics.widthPixels;
        float screenHeight = metrics.heightPixels;
        /*
        Timber.d(TAG, "screenWidth = " + String.valueOf(screenWidth)
                + " px, screenHeight = " + String.valueOf(screenHeight) + " px");
                */
 
        float widthScale = screenWidth / srcWidth;
        float heightScale = screenHeight / srcHeight;
        /*
        Timber.d(TAG, "widthScale = " + String.valueOf(widthScale)
                + ", heightScale = " + String.valueOf(heightScale));
                */
        if (widthScale > heightScale) {
            matrix.postScale(heightScale, heightScale);
        } else {
            matrix.postScale(widthScale, widthScale);
        }
        // リサイズ
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, srcWidth, srcHeight, matrix, true);
        int dstWidth = dst.getWidth(); // 変更後画像のwidth
        int dstHeight = dst.getHeight(); // 変更後画像のheight
        /*
        Timber.d(TAG, "修正後dstWidth = " + String.valueOf(dstWidth)
                + " px, 修正後dstHeight = " + String.valueOf(dstHeight) + " px");
                */
        src = null;
        return dst;
    }
}
