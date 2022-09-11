package com.example.myapplication.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    /**
     * 一级目录 please
     */
    public static String FIRST_DIR_PATH = Environment.getExternalStorageDirectory().getPath()
            +File.separator+"please";

    /**
     * 二级目录 图片images
     */
    private static String  IMAGES_DIR_PATH = FIRST_DIR_PATH+File.separator+"images";

    /**
     * 保存图片
     * @param bitmap
     * @param filePath
     * @return 保存路径
     */
    public static String saveBitmapToFile(Bitmap bitmap, String filePath){
        String picPath=null;
        FileOutputStream fileOutputStream = null;
        try {

            // 新建一级目录
            File firstDir = new File(FIRST_DIR_PATH);
            if(!firstDir.exists()) {
                firstDir.exists();
            }
            //建立二级图片目录
            File imagedir = new File(IMAGES_DIR_PATH);
            if (! imagedir.exists()) {
                imagedir.mkdir();

            }
            //三级分类目录
            String saveDir = IMAGES_DIR_PATH+File.separator+filePath;
            File dir = new File(saveDir);
            if(!dir.exists()) {
                dir.mkdirs();
            }
            // 生成文件名
            SimpleDateFormat t = new SimpleDateFormat("yyyyMMddssSSS");
            String fileName = filePath + (t.format(new Date()))+".png";
            Log.i("TAG","文件名"+fileName);
            // 新建文件
            File file = new File(saveDir, fileName);
            // 打开文件输出流
            fileOutputStream = new FileOutputStream(file);
            Log.i("TAG","文件输出流");
            // 生成图片文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            // 相片的完整路径
            picPath = file.getPath();

            Log.i("TAG","图片存储成功"+picPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return  picPath;
    }


    /**
     *
     * @param fromPath 被复制的文件路径
     * @param toPath 复制的目录文件路径
     * @param rewrite 是否重新创建文件
     *
     * <p>文件的复制操作方法
     */
    public static void copyfile(String fromPath, String toPath, Boolean rewrite ){

        Log.d("copyfile","start");
        Log.d("copyfile",fromPath);
        Log.d("copyfile",toPath);
        File fromFile = new File(fromPath);
        File toFile = new File(toPath);
        Log.d("copyfile",String.valueOf(fromFile));
        Log.d("copyfile",String.valueOf(toFile));

        if(!fromFile.exists()){
            Log.d("copyfile","1");
            return;
        }
        if(!fromFile.isFile()){
            Log.d("copyfile","2");
            return;
        }
        if(!fromFile.canRead()){
            Log.d("copyfile","3");
            return;
        }
        if(!toFile.getParentFile().exists()){
            Log.d("copyfile","4");
            toFile.getParentFile().mkdirs();
        }
        if(toFile.exists() && rewrite){
            Log.d("copyfile","5");
            toFile.delete();
        }

        try {
            Log.d("copyfile","6");
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while((c=fosfrom.read(bt)) > 0){
                fosto.write(bt,0,c);
            }
            Log.d("copyfile","2");
            //关闭输入、输出流
            fosfrom.close();
            fosto.close();

        } catch (FileNotFoundException e) {
            //TODO Auto-generated catch block
            Log.d("fileWrong","1");
            e.printStackTrace();
        } catch (IOException e) {
            //TODO Auto-generated catch block
            Log.d("fileWrong","2");
            e.printStackTrace();
        }

    }

}
