package com.example.boolan.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileService {
    /**
     * 保存图片
     * @param b 图片资源
     * @param strFileName  图片名称
     * @throws IOException
     */
    public  static void savePhoto(Bitmap b, String strFileName){
        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File directory_pictures = new File(sdCard, "Pictures");
            File directory_pictures1 = new File(directory_pictures, "boolan");
            System.out.println(directory_pictures1);
            if(!directory_pictures1.exists()){//判断文件目录是否存在
                directory_pictures1.mkdirs();
            }
            File file = new File(directory_pictures1,strFileName);
            if(!file.isDirectory()){
                file.createNewFile();
            }
            FileOutputStream fos=new FileOutputStream(file);

            if(fos!=null){
                b.compress(Bitmap.CompressFormat.PNG, 80, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 读取图片
     * @param strFileName 图片名称
     * @return 图片内容
     * @throws IOException
     */
    @SuppressWarnings("unused")
    public static Bitmap readPhoto(String strFileName){
        String path= Environment.getExternalStorageDirectory()+"/"+ "Pictures"+"/"+"boolan"+"/"+strFileName;
        if(path!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(path);
            return bitmap;
        }
        else
            return	null;
    }
}
