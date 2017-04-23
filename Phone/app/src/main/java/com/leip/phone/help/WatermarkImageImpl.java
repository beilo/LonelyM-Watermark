package com.leip.phone.help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.jph.takephoto.model.TImage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 被咯苏州 on 2017/4/19.
 */

public class WatermarkImageImpl implements WatermarkListener {
    private ArrayList<TImage> images;
    private WatermarkListener.WatermarkResultListener listener;

    private String filePath;
    private Context context;

    public static WatermarkListener of(Context context, ArrayList<TImage> images, WatermarkListener.WatermarkResultListener listener) {
        return new WatermarkImageImpl(context, images, listener);
    }


    public WatermarkImageImpl(Context context, ArrayList<TImage> images, WatermarkResultListener listener) {
        this.filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "_Watermark_").getPath();
        this.context = context;
        this.images = images;
        this.listener = listener;
    }


    @Override
    public void watermark() {
        watermark(images.get(0));
    }

    private void watermark(final TImage image) {
        // 创建备份文件用于保存水印
        String[] split = image.getOriginalPath().split("/");
        final String imgName = split[split.length - 1];
        String picFileFullName = image.getOriginalPath();
        long fileDate = new File(picFileFullName).lastModified();
        String fileLastModified = DateUtil.currentTime4String(fileDate, DateUtil.DATE_FORMAT_STR4);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(picFileFullName,options);
        Bitmap watermark = ImageUtil.createWatermark(context, bitmap, fileLastModified, 0);//添加水印
        FileUtils.saveBitmapFile(watermark, filePath + imgName);

        continueWatermark(image);
    }


    private void continueWatermark(TImage image, String... message) {
        int index = images.indexOf(image);
        boolean isLast;
        if (index == images.size() - 1) {
            isLast = true;
        } else {
            isLast = false;
        }
        if (isLast) {
            handleWatermarkCallBack(message);
        } else {
            watermark(images.get(index + 1));
        }
    }

    private void handleWatermarkCallBack(String... message) {
        if (message.length > 0) {
            listener.onWatermarkFailed(images, message[0]);
            return;
        }
        listener.onWatermarkSuccess(images);
    }
}
