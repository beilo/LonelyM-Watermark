package com.leip.phone.help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.leip.phone.R;

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
        LayoutInflater mInflater = LayoutInflater.from(context);
        View viewLayout = mInflater.inflate(R.layout.view_item, null);


        Bitmap watermark = WaterMarkUtil.of(context).createViewWatermark(bitmap,viewLayout);//添加水印
//        Bitmap watermark = WaterMarkUtil.of(context).createWatermark(bitmap, "hahahah", "11111111", 0);//添加水印
        // 保存图片
        File fileWater = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM + "/czcg/watermark/" + imgName);
        if (!fileWater.getParentFile().exists())
            fileWater.getParentFile().mkdirs();
        String path = fileWater.getPath();
        WaterMarkUtil.of(context).saveBitmapFile(watermark, path);
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
