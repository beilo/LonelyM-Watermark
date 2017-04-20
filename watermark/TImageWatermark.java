package com.czcg.gwt.listener;


import android.net.Uri;

/**
 * Created by 被咯苏州 on 2017/4/20.
 */

public class TImageWatermark{
    private String originalPath;
    private String compressPath;
    private String watermarkPath; // 水印路径

    private boolean cropped;
    private boolean compressed;
    private boolean watermark;

    private FromType fromType;
    public static TImageWatermark of(String path, FromType fromType){
        return new TImageWatermark(path, fromType);
    }
    public static TImageWatermark of(Uri uri, FromType fromType){
        return new TImageWatermark(uri, fromType);
    }
    private TImageWatermark(String path, FromType fromType) {
        this.originalPath = path;
        this.fromType = fromType;
    }
    private TImageWatermark(Uri uri, FromType fromType) {
        this.originalPath = uri.getPath();
        this.fromType = fromType;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getCompressPath() {
        return compressPath;
    }

    public void setCompressPath(String compressPath) {
        this.compressPath = compressPath;
    }

    public String getWatermarkPath() {
        return watermarkPath;
    }

    public void setWatermarkPath(String watermarkPath) {
        this.watermarkPath = watermarkPath;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public boolean isWatermark() {
        return watermark;
    }

    public void setWatermark(boolean watermark) {
        this.watermark = watermark;
    }

    public FromType getFromType() {
        return fromType;
    }

    public void setFromType(FromType fromType) {
        this.fromType = fromType;
    }

    public enum FromType {
        CAMERA, OTHER
    }
}
