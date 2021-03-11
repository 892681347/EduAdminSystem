package com.zyh.utills;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

public class FileUtil {
    /**
     * 将jpeg图片修改为jpg和png
     */
    public static File modifyFileName(File pathFile) {
        //图片命名的时间戳
        long totalMilliSeconds = System.currentTimeMillis();
        String str = String.valueOf(totalMilliSeconds);
        //获取文件名字
        String fileName = pathFile.getName();
        String s = fileName.toLowerCase();
        if (s.contains(".jpeg")) {
            String oldPath = pathFile.getAbsolutePath();
            if (!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(fileName)) {
                String newPath = pathFile.getAbsolutePath() + ".png";
                newPath = oldPath.replace(fileName, str + ".jpg");
                File file2 = renameFile(oldPath, newPath);
                return file2;
            } else {
                return null;
            }
        } else {
            return pathFile;
        }
    }
    /**
     * oldPath 和 newPath必须是新旧文件的绝对路径
     */
    private static File renameFile(String oldPath, String newPath) {
        if (TextUtils.isEmpty(oldPath)) {
            return null;
        }

        if (TextUtils.isEmpty(newPath)) {
            return null;
        }
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);
        boolean b = oldFile.renameTo(newFile);
        File file2 = new File(newPath);
        return file2;
    }
    public static boolean isPicLegal(File pic,double size){
        String fileName = pic.getName();
        String s = fileName.toLowerCase();
        if (!(s.contains(".jpeg")||s.contains(".jpg")||s.contains(".png"))) return false;
        else if (getFileOrFilesSize(pic) > size) return false;
        return true;
    }
    public static double getFileOrFilesSize(File file){
        long blockSize=0;
        try {
            if(file.isDirectory()){
                blockSize = getFileSizes(file);
            }else{
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FormetFileSize(blockSize);
    }
    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception
    {
        long size = 0;
        if (file.exists()){
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        else{
            file.createNewFile();
            Log.e("获取文件大小","文件不存在!");
        }
        return size;
    }
    /**
     * 获取指定文件夹
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception
    {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++){
            if (flist[i].isDirectory()){
                size = size + getFileSizes(flist[i]);
            }
            else{
                size =size + getFileSize(flist[i]);
            }
        }
        return size;
    }
    /**
     * 转换文件大小为MB
     * @param fileS
     * @return
     */
    private static double FormetFileSize(long fileS)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        fileSizeLong=Double.valueOf(df.format((double) fileS / 1048576));
        return fileSizeLong;
    }
    /**
     * 将URI转为图片的路径
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    //压缩图片
    public static File compress( File originFile) {
        if(getFileOrFilesSize(originFile)<=0.8) return originFile;
        File newPic;
        File sdFile = Environment.getExternalStorageDirectory();
        Bitmap bitmap = BitmapFactory.decodeFile(originFile.getAbsolutePath());
        //设置缩放比
        int radio = 8;
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth() / radio, bitmap.getHeight() / radio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        RectF rectF = new RectF(0, 0, bitmap.getWidth() / radio, bitmap.getHeight() / radio);
        //将原图画在缩放之后的矩形上
        canvas.drawBitmap(bitmap, null, rectF, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        result.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        try {
            newPic = new File(sdFile, "sizeCompress.jpg");
            FileOutputStream fos = new FileOutputStream(newPic);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return newPic;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
