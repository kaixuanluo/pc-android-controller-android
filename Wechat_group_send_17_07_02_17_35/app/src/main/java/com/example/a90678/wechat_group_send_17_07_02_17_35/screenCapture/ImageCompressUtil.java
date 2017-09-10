package com.example.a90678.wechat_group_send_17_07_02_17_35.screenCapture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.example.a90678.wechat_group_send_17_07_02_17_35.utils.L;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 描述说明  <br/>
 * Author : luokaixuan <br/>
 * CreateDate : 9/18/15 10:11 AM<br/>
 * Modified : luokaixuan <br/>
 * ModifiedDate : 9/18/15 10:11 AM<br/>
 * Email : 1005949566@qq.com <br/>
 * Version 1.0
 */
public class ImageCompressUtil {

    public static File getThumbUploadPath(File uploadFile) throws Exception {

        Bitmap bbb = getThumbUploadPathBitmap(uploadFile);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        return saveImg(bbb, timeStamp);
    }

    public static File getThumbUploadPath(String filePath) throws Exception {
        return getThumbUploadPath(new File(filePath));
    }

    public static Bitmap getThumbUploadPathBitmap(File uploadFile) throws Exception {
        int bitmapMaxWidth = 270;
        String oldPath = uploadFile.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(oldPath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int reqHeight = 0;
        int reqWidth = bitmapMaxWidth;
        reqHeight = (reqWidth * height)/width;
        // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
//        options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth, reqHeight);
//        options.inSampleSize = calculateInSampleSize(options, 270, 480);
//                Timber.d("calculateInSampleSize(options, 480, 800);==="
//                                + calculateInSampleSize(options, 480, 800));
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
        //Log.e("asdasdas", "reqWidth->"+reqWidth+"---reqHeight->"+reqHeight);
        if (bitmap == null) {
            return null;
        }
        Bitmap bbb = compressImage(Bitmap.createScaledBitmap(bitmap, bitmapMaxWidth, reqHeight, true));

        //旋转图片
        int orientation = readPictureDegree(oldPath);//获取旋转角度

        if(Math.abs(orientation) > 0){
            bbb = rotaingImageView(orientation, bbb);//旋转图片
        }

        return bbb;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                      int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 5) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            if (options >10) {
                options -= 10;// 每次都减少10
            } else {
                break;
            }
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 得到bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    /**
         * @param b Bitmap
         * @return 图片存储的位置
         */
        public static File saveImg(Bitmap b, String name) throws Exception {
            String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "test/headImg/";
            File mediaFile = new File(path + File.separator + name + ".jpg");
            if (mediaFile.exists()) {
                mediaFile.delete();

            }
            if (!new File(path).exists()) {
                new File(path).mkdirs();
            }
            mediaFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(mediaFile);
            b.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            b.recycle();
            b = null;
            System.gc();
//            return mediaFile.getPath();
            return mediaFile;
        }

    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 图片的压缩
     *
     * @param outputFile
     * @return
     */

    public static File scal(File outputFile) {

        long fileSize = outputFile.length();
        final long fileMaxSize = 5 * 1024;
        if (fileSize >= fileMaxSize) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            String oldPath = outputFile.getAbsolutePath();
            BitmapFactory.decodeFile(oldPath, options);
            int height = options.outHeight;
            int width = options.outWidth;

            double scale = Math.sqrt((float) fileSize / fileMaxSize);
            options.outHeight = (int) (height / scale);
            options.outWidth = (int) (width / scale);
            options.inSampleSize = (int) (scale + 0.5);
            options.inJustDecodeBounds = false;

            Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
            outputFile = new File(createImageFile().getPath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.d("", "sss ok " + outputFile.length());
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            } else {
                File tempFile = outputFile;
                outputFile = new File(createImageFile().getPath());
                copyFileUsingFileChannels(tempFile, outputFile);
            }

        }
        return outputFile;

    }
//    public static Bitmap scal2Bitmap(File outputFile) {
//
//        long fileSize = outputFile.length();
//        final long fileMaxSize = 5 * 1024;
//        if (fileSize >= fileMaxSize) {
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            String oldPath = outputFile.getAbsolutePath();
//            BitmapFactory.decodeFile(oldPath, options);
//            int height = options.outHeight;
//            int width = options.outWidth;
//
//            double scale = Math.sqrt((float) fileSize / fileMaxSize);
//            options.outHeight = (int) (height / scale);
//            options.outWidth = (int) (width / scale);
//            options.inSampleSize = (int) (scale + 0.5);
//            options.inJustDecodeBounds = false;
//
//            Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);
//            outputFile = new File(createImageFile().getPath());
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(outputFile);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                fos.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            Log.d("", "sss ok " + outputFile.length());
//            if (!bitmap.isRecycled()) {
//                bitmap.recycle();
//            } else {
//                File tempFile = outputFile;
//                outputFile = new File(createImageFile().getPath());
//                copyFileUsingFileChannels(tempFile, outputFile);
//            }
//
//        }
//        return outputFile;
//
//    }

    /**
     * 一切都操作uri
     *
     * @return
     */
    public static Uri createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Save Reboot file: path for use with ACTION_VIEW intents
        return Uri.fromFile(image);
    }

    public static void copyFileUsingFileChannels(File source, File dest) {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } finally {
            try {
                inputChannel.close();
                outputChannel.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    public static File getRotateFile(File uploadFile) throws Exception {
        int bitmapMaxWidth = 150;
        String oldPath = uploadFile.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(oldPath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int reqHeight = 0;
        int reqWidth = bitmapMaxWidth;
        reqHeight = (reqWidth * height)/width;
        // 在内存中创建bitmap对象，这个对象按照缩放大小创建的
//        options.inSampleSize = calculateInSampleSize(options, bitmapMaxWidth, reqHeight);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
//                Timber.d("calculateInSampleSize(options, 480, 800);==="
//                                + calculateInSampleSize(options, 480, 800));
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(oldPath, options);

        //旋转图片
        int orientation = readPictureDegree(oldPath);//获取旋转角度

        if(Math.abs(orientation) > 0){
            bitmap = rotaingImageView(orientation, bitmap);//旋转图片
        }

        return saveImg(bitmap, uploadFile.getName());
    }
}
