package com.czcg.gwt.listener;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import com.czcg.gwt.util.DateUtil;
import com.czcg.gwt.util.FileUtils;
import com.czcg.gwt.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 被咯苏州 on 2017/4/19.
 */

public class WatermarkImageImpl implements WatermarkListener {
    private ArrayList<TImageWatermark> images;
    private WatermarkListener.WatermarkResultListener listener;

    private Context context;

    public static WatermarkListener of(Context context, ArrayList<TImageWatermark> images, WatermarkListener.WatermarkResultListener listener) {
        return new WatermarkImageImpl(context, images, listener);
    }


    public WatermarkImageImpl(Context context, ArrayList<TImageWatermark> images, WatermarkResultListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }


    @Override
    public void watermark() {
        if (images == null || images.isEmpty())
            listener.onWatermarkFailed(images, " images is null");
        for (TImageWatermark image : images) {
            if (image == null) {
                listener.onWatermarkFailed(images, " There are pictures of compress  is null.");
                return;
            }
        }
        watermark(images.get(0));
    }

    private void watermark(final TImageWatermark image) {
        if (TextUtils.isEmpty(image.getOriginalPath())) {
            continueWatermark(image, false);
            return;
        }
        File file = new File(image.getOriginalPath());
        if (file == null || !file.exists() || !file.isFile()) {
            continueWatermark(image, false);
            return;
        }
        // 创建备份文件用于保存水印
        String[] split = image.getOriginalPath().split("/");
        final String imgName = split[split.length - 1];
        String picFileFullName = image.getOriginalPath();
        Bitmap bitmap = BitmapFactory.decodeFile(picFileFullName);
        // 添加水印
        long fileDate = new File(picFileFullName).lastModified();
        String[] fileLastModified = DateUtil.currentTime4String(fileDate, DateUtil.DATE_FORMAT_STR4).split(" ");
        Bitmap watermark = ImageUtil.createWatermark(context, bitmap, fileLastModified[0], fileLastModified[1], 0);//添加水印
        // 保存图片
        File fileWater = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + "/czcg/watermark/" + imgName);
        if (!fileWater.getParentFile().exists()) fileWater.getParentFile().mkdirs();
        String path = fileWater.getPath();
        FileUtils.saveBitmapFile(watermark, path);
        // 保存数据
        image.setWatermarkPath(path);
        continueWatermark(image, true);
    }


    private void continueWatermark(TImageWatermark image, boolean preSuccess, String... message) {
        image.setWatermark(preSuccess);
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
