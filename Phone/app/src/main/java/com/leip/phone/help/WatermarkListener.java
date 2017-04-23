package com.leip.phone.help;

import com.jph.takephoto.model.TImage;

import java.util.ArrayList;

/**
 * Created by 被咯苏州 on 2017/4/19.
 */

public interface WatermarkListener {
    void watermark();

    /**
     * 水印结果监听器
     */
    interface WatermarkResultListener {
        /**
         * 压缩成功
         * @param images 已经添加水印图片
         */
        void onWatermarkSuccess(ArrayList<TImage> images);

        /**
         * 添加失败
         * @param images 压缩失败的图片
         * @param msg 失败的原因
         */
        void onWatermarkFailed(ArrayList<TImage> images, String msg);
    }
}
