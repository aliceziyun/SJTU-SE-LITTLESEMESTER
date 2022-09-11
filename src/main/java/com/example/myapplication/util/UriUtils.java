package com.example.myapplication.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

public class UriUtils {
    /**
     * 将Uri转换成Bitmap
     */
    public static Bitmap decodeUriAsBitmap(Context context, Uri uri){

        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return bitmap;
    }


    /**
     * 将Uri转为路径地址
     */
    public static String getRealFilePath( final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = new String();
        Log.d("getRealFilePath data",data);
        if ( scheme == null ) {
            Log.d("getRealFilePath","1");
            data = uri.getPath();
        }
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            Log.d("getRealFilePath","2");
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Log.d("getRealFilePath","3");
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            Log.d("getRealFilePath index",String.valueOf(cursor.getCount()));
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    Log.d("getRealFilePath","4");
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        Log.d("getRealFilePath","5");
                        Log.d("getRealFilePath",String.valueOf(index));
                        data = cursor.getString(index);
                        if(data == null)
                            Log.d("getRealFilePath data","null");
                    }
                }
                cursor.close();
            }
        }
        Log.d("getRealFilePath",data);
        return data;
    }


    /**
     * 将路径地址转换为Uri
     */
    public static Uri getUriFromFilePath(final String path){

        return Uri.fromFile(new File(path));
    }
}
