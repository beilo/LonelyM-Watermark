package com.leip.phone;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.leip.phone.help.TImageWatermark;
import com.leip.phone.help.WatermarkImageImpl;
import com.leip.phone.help.WatermarkListener;

import java.io.File;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements TakePhoto.TakeResultListener, InvokeListener {
    public static String TAG = "MainActivity";
    private Context context;

    private ImageView imageView;
    private Button btn_photo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.img);
        btn_photo = (Button) findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photo(v);
            }
        });
        context = this;
    }

    public void photo(View view) {
//        String timeteamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(new Date());
        String timeteamp = String.valueOf(System.currentTimeMillis());
        String imageFileName = "CZCG_JPEG_" + timeteamp + ".png";
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "_Original_" + imageFileName);
        Uri uri = Uri.fromFile(file);
        takePhoto.onPickFromCapture(uri);
    }

    @Override
    public void takeSuccess(TResult result) {
        String originalPath = result.getImage().getOriginalPath();
        ArrayList<TImageWatermark> tImages = new ArrayList<>();
        tImages.add(TImageWatermark.of(originalPath, TImageWatermark.FromType.OTHER));

        WatermarkImageImpl.of(context, tImages, new WatermarkListener.WatermarkResultListener() {
            @Override
            public void onWatermarkSuccess(ArrayList<TImageWatermark> images) {
                Glide.with(context)
                        .load(images.get(0).getWatermarkPath())
                        .into(imageView);
            }

            @Override
            public void onWatermarkFailed(ArrayList<TImageWatermark> images, String msg) {

            }
        }).watermark();
        Log.e(TAG, "takeSuccess: " + originalPath);
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod());
        if (PermissionManager.TPermissionType.WAIT.equals(type)) {
            this.invokeParam = invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //以下代码为处理Android6.0、7.0动态权限所需
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this);
    }

    public TakePhoto getTakePhoto() {
        if (takePhoto == null) {
            takePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this, this));
        }
        return takePhoto;
    }
}
