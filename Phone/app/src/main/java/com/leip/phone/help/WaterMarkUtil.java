package com.leip.phone.help;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 被咯苏州 on 2017/4/14.
 */

public class WaterMarkUtil {

    private Context mContext;

    private WaterMarkUtil(Context mContext) {
        this.mContext = mContext;
    }

    public static WaterMarkUtil of(Context context) {
        return new WaterMarkUtil(context);
    }

    /**
     * 添加水印
     *
     * @param bitmap       原图
     * @param markText     水印文字
     * @param markBitmapId 水印图片
     * @return bitmap      打了水印的图
     */
    public Bitmap createWatermark(Bitmap bitmap, String markText, String markText2, int markBitmapId) {
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
            float scale = mContext.getResources().getDisplayMetrics().density;
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
            float textX2 = bitmapWidth - textBounds2.width() - 50;
            float textY2 = bitmapHeight - textBounds2.height() + 6;
            canvas.drawText(markText2, textX2, textY2, mPaint);

            // 文字开始的坐标 -----图片高宽-文字高宽
            textX = bitmapWidth - textBounds.width() - 50;//这里的-10和下面的+6都是微调的结果
            textY = bitmapHeight - textBounds.height() - textBounds2.height() - 12;

            // 画文字
            canvas.drawText(markText, textX, textY, mPaint);

        }

        //------------开始绘制图片-------------------------

        if (markBitmapId != 0) {
            // 载入水印图片
            Bitmap markBitmap = BitmapFactory.decodeResource(mContext.getResources(), markBitmapId);

            // 如果图片的大小小于水印的3倍，就不添加水印
            // if (markBitmap.getWidth() > bitmapWidth / 3 || markBitmap.getHeight() > bitmapHeight / 3) {
            //     return bitmap;
            // }

            int markBitmapWidth = markBitmap.getWidth();
            int markBitmapHeight = markBitmap.getHeight();

            // 图片开始的坐标
            float bitmapX = (float) (bitmapWidth - markBitmapWidth - 50);//这里的-10和下面的-20都是微调的结果
            float bitmapY = (float) (textY - markBitmapHeight - 20);

            // 画图
            canvas.drawBitmap(markBitmap, bitmapX, bitmapY, null);
        }

        //保存所有元素
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        if (!bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;
    }

    public Bitmap createViewWatermark(Bitmap bitmap, @NonNull View view) {
        if (view == null) {
            return bitmap;
        }
        // 获取图片的宽高
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        //
        // 创建一个和图片一样大的背景图
        Bitmap bmp = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        // 画背景图
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.save();
        // 轮询view的节点
        ViewGroup vg = (ViewGroup) view;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View childAt = vg.getChildAt(i);
            ViewGroup.LayoutParams params = childAt.getLayoutParams();
            if (params.width == ViewGroup.LayoutParams.MATCH_PARENT) {
                params.width = bitmap.getWidth();
                vg.getChildAt(i).setLayoutParams(params);
            }
        }
        // 设置最小高宽
        view.setMinimumWidth(bitmap.getWidth());
        view.setMinimumHeight(bitmap.getHeight());
        // 设置测量尺寸
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 创建一个跟view一样大小的空bitmap
        Bitmap bitmapView = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(bitmapView, 0, 0, null);
        // 设置view相对于父类的位置
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        // 执行绘画view
        view.draw(canvas);
        canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.restore();
        if (!bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        return bmp;

    }

    // Bitmap对象保存图片文件
    public void saveBitmapFile(Bitmap bitmap, String url) {
        File file = new File(url);//将要保存图片的路径
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!bitmap.isRecycled()){
                // 回收并且置为null
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
}
