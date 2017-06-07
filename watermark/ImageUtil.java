package com.czcg.gwt.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;

/**
 * Created by 被咯苏州 on 2017/4/14.
 */

public class ImageUtil {
    /**
     * 添加水印
     *
     * @param context      上下文
     * @param bitmap       原图
     * @param markText     水印文字
     * @param markBitmapId 水印图片
     * @return bitmap      打了水印的图
     */
    public static Bitmap createWatermark(Context context, Bitmap bitmap, String markText, String markText2, int markBitmapId) {

        // 当水印文字与水印图片都没有的时候，返回原图
        if (TextUtils.isEmpty(markText) && markBitmapId == 0) {
            return bitmap;
        }
        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        // 画背景图
        canvas.drawBitmap(bitmap, 0, 0, null);
        //-------------开始绘制文字-------------------------------

        // 文字开始的坐标,默认为左上角
        float textX = 10;
        float textY = 0;

        if (!TextUtils.isEmpty(markText)) {
            // 创建画笔
            Paint mPaint = new Paint();
            // 文字矩阵区域
            Rect textBounds = new Rect();
            // 获取屏幕的密度，用于设置文本大小
            float scale = context.getResources().getDisplayMetrics().density;
            // 水印的字体大小
            // mPaint.setTextSize((int) (18 * scale));
            mPaint.setTextSize(bitmapWidth / 30);
            // 文字阴影
            mPaint.setShadowLayer(0.5f, 0f, 1f, Color.BLACK);
            // 抗锯齿
            mPaint.setAntiAlias(true);
            // 水印的区域
            mPaint.getTextBounds(markText, 0, markText.length(), textBounds);
            // 水印的颜色
            mPaint.setColor(Color.WHITE);

            // 当图片大小小于文字水印大小的3倍的时候，不绘制水印
            if (textBounds.width() > bitmapWidth / 3 || textBounds.height() > bitmapHeight / 3) {
                return bitmap;
            }

            Rect textBounds2 = new Rect();
            mPaint.getTextBounds(markText2, 0, markText2.length(), textBounds2);
            float textX2 = bitmapWidth - textBounds2.width() - 10;
            float textY2 = bitmapHeight - textBounds2.height() + 6;
            canvas.drawText(markText2, textX2, textY2, mPaint);

            // 文字开始的坐标 -----图片高宽-文字高宽
            textX = bitmapWidth - textBounds.width() - 10;//这里的-10和下面的+6都是微调的结果
            textY = bitmapHeight - textBounds.height() - textBounds2.height() - 12;

            // 画文字
            canvas.drawText(markText, textX, textY, mPaint);

        }

        //------------开始绘制图片-------------------------

        if (markBitmapId != 0) {
            // 载入水印图片
            Bitmap markBitmap = BitmapFactory.decodeResource(context.getResources(), markBitmapId);

            // 如果图片的大小小于水印的3倍，就不添加水印
            // if (markBitmap.getWidth() > bitmapWidth / 3 || markBitmap.getHeight() > bitmapHeight / 3) {
            //     return bitmap;
            // }

            int markBitmapWidth = markBitmap.getWidth();
            int markBitmapHeight = markBitmap.getHeight();

            // 图片开始的坐标
            float bitmapX = (float) (bitmapWidth - markBitmapWidth - 10);//这里的-10和下面的-20都是微调的结果
            float bitmapY = (float) (textY - markBitmapHeight - 20);

            // 画图
            canvas.drawBitmap(markBitmap, bitmapX, bitmapY, null);
        }

        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        if(!bitmap.isRecycled()){
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;
    }
}
