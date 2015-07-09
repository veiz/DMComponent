package com.dimo.share.utils;

import java.io.*;
import android.content.*;
import android.database.*;
import android.graphics.*;
import android.net.Uri;
import android.provider.*;

public class BitmapUtil {

/******************
 * 端末内の画像を取得する時に使用
 * Uriから画像を取得
 * @param context
 * @param uri
 * @return
 * @throws IOException
 */
	private static final int RECORD_IMG_WIDTH = 640,
			          RECORD_IMG_HEIGHT = 480;
	
	public static  Bitmap createBitmap(Context context,Uri uri)throws IOException{
		Bitmap bitmap = null;
	    InputStream is = null;
	    try{
	    	BitmapFactory.Options options = getBmpOptions(context,uri);
	    	is = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is,null,options);

	    }catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(is != null) is.close();
		}
		return bitmap;
	}
	
	public static  Bitmap createBitmap(ContentResolver resolver,Uri uri)throws IOException{
		Bitmap bitmap = null;
	    InputStream is = null;
	    try{
	    	BitmapFactory.Options options = getBmpOptions(resolver,uri);
	    	is = resolver.openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is,null,options);

	    }catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(is != null) is.close();
		}
		return bitmap;
	}
	
	public static  Bitmap createBitmap(Context context,Uri uri,Matrix matrix,float ang)throws IOException{
		 Bitmap src = null;
		
	    InputStream is = null;
	    try{
	    	BitmapFactory.Options options = new BitmapFactory.Options();
	    	//options.inJustDecodeBounds = true;
	    	is = context.getContentResolver().openInputStream(uri);
	    	
			Bitmap bitmap = BitmapFactory.decodeStream(is,null,options);
			 int bx = options.outWidth/2;
             int by = options.outHeight/2;
	             matrix.postTranslate(-bx,-by);
	             matrix.postRotate(ang);
			   matrix.postTranslate(bx,by);
			  // options.inJustDecodeBounds = false;
			   src = Bitmap.createBitmap(bitmap, 0,0,bx, by,matrix,false);
			  // bitmap.recycle();
			  // bitmap = null;

	    }catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(is != null) is.close();
		}
		return src;
	}


    /*************
     * 画像サイズの制限
     * URI内の画像を決まった画像サイズに最適化
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */

	private static BitmapFactory.Options getBmpOptions(Context context,Uri uri)throws IOException{
		BitmapFactory.Options options = new BitmapFactory.Options();
		InputStream io = null;
		try{
			options.inJustDecodeBounds = true;
			io = context.getContentResolver().openInputStream(uri);
			BitmapFactory.decodeStream(io,null,options);
			/*
		boolean landscape = options.outWidth > options.outHeight;
		final int dw = ImageStorage.RECORD_IMG_WIDTH;
		final int dh = ImageStorage.RECORD_IMG_HEIGHT;
		int h = landscape ? dw : dh;
		int w = landscape ? dh : dw;
		int scale = Math.max((int) Math.floor(options.outWidth / w),(int) Math.floor(options.outHeight / h));
		*/
		options.inSampleSize = calculateInSampleSize(options, RECORD_IMG_WIDTH,RECORD_IMG_HEIGHT);
		options.inJustDecodeBounds = false;
		
		
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(io != null) io.close();
		}
		return options;
	}
	
	private static BitmapFactory.Options getBmpOptions(ContentResolver resolver,Uri uri)throws IOException{
		BitmapFactory.Options options = new BitmapFactory.Options();
		InputStream io = null;
		try{
			options.inJustDecodeBounds = true;
			io = resolver.openInputStream(uri);
			BitmapFactory.decodeStream(io,null,options);
			/*
		boolean landscape = options.outWidth > options.outHeight;
		final int dw = ImageStorage.RECORD_IMG_WIDTH;
		final int dh = ImageStorage.RECORD_IMG_HEIGHT;
		int h = landscape ? dw : dh;
		int w = landscape ? dh : dw;
		int scale = Math.max((int) Math.floor(options.outWidth / w),(int) Math.floor(options.outHeight / h));
		*/
		options.inSampleSize = calculateInSampleSize(options, RECORD_IMG_WIDTH,RECORD_IMG_HEIGHT);
		options.inJustDecodeBounds = false;
		
		
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(io != null) io.close();
		}
		return options;
	}
	/**********
	 * 極力等倍サイズで 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

	    // 画像の元サイズ
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {
	        if (width > height) {
	            inSampleSize = Math.round((float)height / (float)reqHeight);
	        } else {
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }
	    }
	    return inSampleSize;
	}


	/*************
	 * 画像の最適化
	 * 元画像を縮小した画像を返す
	 * @param bmp
	 * @return
	 */
	public static Bitmap scaleChange(Bitmap bmp,int scaleSize){
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		float scale = Math.max((float)scaleSize/w, (float)scaleSize/h);
		int size = Math.max(w, h);
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		Bitmap bmp2 = Bitmap.createBitmap(bmp, (w-size)/2, (h-size)/2, size, size, matrix, true);
		return bmp2;
	}


     /**************
      * 端末内画像
      * 指定されたピクセルサイズに元画像を縮小させ最適化して入力ストリームを返す
      * @param context
      * @param imgName
      * @param uri
      * @param scaleSize
      * @return
      */

	public static InputStream getSacleChangeByteStream(Context context,String imgName,Uri uri,int scaleSize){

		Bitmap bitmap = null;
	    InputStream is = null;
	    try{
	    	BitmapFactory.Options options = getBmpOptions(context,uri);
	    	is = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is,null,options);
	    }catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
		}

		Bitmap scale = scaleChange(bitmap, scaleSize);
		bitmap.recycle();
		if(scale != null){
			return getByteStream(scale,imgName);
		}

		return null;

	}
	
	public static InputStream getByteStream(Context context,String imgName,Uri uri){

		Bitmap bitmap = null;
	    InputStream is = null;
	    try{
	    	BitmapFactory.Options options = getBmpOptions(context,uri);
	    	is = context.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(is,null,options);
	    }catch (Exception e) {
			// TODO: handle exception
		}finally{
			if(is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
		}	
			return getByteStream(bitmap,imgName);
	

	}
    /***********
     * 受信した画像をバイナリコードでストリームをして返す
     * @param bmp
     * @param imgName
     * @return
     */
	public static InputStream getByteStream(Bitmap bmp,String imgName){
         ByteArrayOutputStream jpg = new ByteArrayOutputStream(bmp.getRowBytes());        
         String exten = getExtention(imgName);
         boolean flg;
         if(exten.endsWith("png") || exten.endsWith("PNG")){
        	 flg = bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg);        	
         }else{
            flg = bmp.compress(Bitmap.CompressFormat.JPEG, 100, jpg);
         }
         //if(bmp != null && flg) bmp.recycle();
         return new ByteArrayInputStream(jpg.toByteArray());
	}
/*
	public static InputStream getByteStream(Context context,String imgName,Uri uri){
		 Bitmap bmp = null;
		try {
			bmp = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
         ByteArrayOutputStream jpg = new ByteArrayOutputStream(bmp.getRowBytes());
         String[] split = imgName.split(".");
         if(split[1].equals("png") || split[1].equals("PNG")){
        	 bmp.compress(Bitmap.CompressFormat.PNG, 100, jpg);
         }else{
             bmp.compress(Bitmap.CompressFormat.JPEG, 100, jpg);
         }
        // if(bmp != null) bmp.recycle();
         return new ByteArrayInputStream(jpg.toByteArray());
	}
	*/


	// ByteArray を Bitmap に変換
	public static Bitmap getBitmapFromByteArray(byte[] b) {
	    return BitmapFactory.decodeByteArray(b, 0, b.length);
	}

	 /********
     *文字列中の拡張子だけ抜き出す。
     */
	public static String getExtention(String fileName){
		char[] c = fileName.toCharArray();
		int len = c.length-1;
		int size = 0;
		if(fileName.endsWith("jpeg")){
			size = 4;
		}else if(fileName.endsWith("jpg") || fileName.endsWith("png") || fileName.endsWith("gif")){
			size = 3;
		}
		char[] l = new char[size];// 拡張子 文字数分 
		int cnt = l.length-1;
		for(int i=len;i >= 0;i--){
			if(c[i] == 46) break;
			   l[cnt] = c[i];
			   cnt--;
		}		
		return String.valueOf(l);
	}
	
	public static Bitmap loadBitmap(Context context, Uri uri, int viewWidth, int viewHeight) {
	    // Uriから画像を読み込みBitmapを作成
	    Bitmap originalBitmap = null;
	    try {
	        originalBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	 
	    // MediaStoreから回転情報を取得
	    final int orientation;
	    Cursor cursor = MediaStore.Images.Media.query(context.getContentResolver(), uri, new String[] {
	        MediaStore.Images.ImageColumns.ORIENTATION
	    });
	    if (cursor != null) {
	        cursor.moveToFirst();
	        orientation = cursor.getInt(0);
	    } else {
	        orientation = 0;
	    }
	 
	    final int originalWidth = originalBitmap.getWidth();
	    final int originalHeight = originalBitmap.getHeight();
	 
	    // 縮小割合を計算
	    final float scale;
	    if (orientation == 90 || orientation == 270) {
	        scale = Math.min((float)viewWidth / originalHeight, (float)viewHeight / originalWidth);
	    } else {
	        scale = Math.min((float)viewWidth / originalWidth, (float)viewHeight / originalHeight);
	    }
	 
	    // 変換行列の作成
	    final Matrix matrix = new Matrix();
	    if (orientation != 0) {
	        matrix.postRotate(orientation);
	    }
	    if (scale < 1.0f) {
	        matrix.postScale(scale, scale);
	    }
	 
	    // 行列によって変換されたBitmapを返す
	    return Bitmap.createBitmap(originalBitmap, 0, 0, originalWidth, originalHeight, matrix,
	            true);
	}
	
	
	/*****************
	 * 画像データをアプリフォルダーに格納
	 * @param context
	 * @param saveImage
	 * @throws IOException
	 */
	/*
	public static Uri saveBitmap(Context context,Bitmap saveImage) throws IOException {

	    final String SAVE_DIR = ExternalAppOpener.getSaveDirectory();
	    File file = new File(SAVE_DIR);
	    try{
	        if(!file.exists()){
	            file.mkdir();
	        }
	    }catch(SecurityException e){
	        e.printStackTrace();
	        throw e;
	    }

	    Date mDate = new Date();
	    SimpleDateFormat fileNameDate = new SimpleDateFormat("yyyyMMdd_HHmmss");
	    String fileName = fileNameDate.format(mDate) + ".jpg";
	    String AttachName = file.getAbsolutePath() + "/" + fileName;

	    try {
	        FileOutputStream out = new FileOutputStream(AttachName);
	        saveImage.compress(CompressFormat.JPEG, 100, out);
	        out.flush();
	        out.close();
	    } catch(IOException e) {
	        e.printStackTrace();
	        throw e;
	    }
	    
	        // save index
	    ContentValues values = new ContentValues();
	    ContentResolver contentResolver = context.getContentResolver();
	    values.put(MediaColumns.MIME_TYPE, "image/jpeg");
	    values.put(MediaColumns.TITLE, fileName); 
	    values.put("_data", AttachName);
	    return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
	}
	*/




}
