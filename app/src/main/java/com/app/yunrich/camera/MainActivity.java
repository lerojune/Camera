package com.app.yunrich.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    final int REQ_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void camera(){
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQ_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            finish();
            return;
        }
        switch (requestCode) {
            case REQ_CAMERA:
                if (!data.hasExtra("data"))
                    return;
                Bitmap bmp = data.getParcelableExtra("data");
//                saveImage(bmp);
//                setResult(RESULT_OK,new Intent().putExtra("result",cameraFile));
//                finish();
                break;
            default:
                break;
        }
    }

/*
    @Override
    protected void initView() {
        super.initView();
        holder = surfaceView.getHolder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                PermissionsUtil.requestPermissions(instance, Manifest.permission.CAMERA);
            } else {
                holder.addCallback(callback);
            }
        } else {
            if (Build.VERSION.SDK_INT >= 19){
                AppOpsManager opsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                try {
                    Method dispatchMethod = AppOpsManager.class.getMethod(
                            "checkOpNoThrow", new Class[] { int.class, int.class,
                                    String.class });
                    int mode = (Integer) dispatchMethod.invoke(opsManager,
                            new Object[] {26, Binder.getCallingUid(),
                                    getPackageName() });
                    if (mode == 1){
                        //DialogUtil.showMsgDialog(//instance,"本应用需要相机权限。你可以在设置->权限管理->相机中打开相应的权限","确定",null);
                    }else{
                        //holder.addCallback(callback);
                    }
                } catch (NoSuchMethodException e){
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                ////holder.addCallback(callback);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int index = 0; index < permissions.length; index++) {
            if (permissions[index].equals(Manifest.permission.CAMERA)) {
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    holder.addCallback(callback);
                } else {
                    DialogUtil.showMsgDialog(instance,"本应用需要相机权限。你可以在设置->权限管理->相机中打开相应的权限","确定",null);
                }
            }
        }
    }


    SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        //优化图片质量
        private void setOptionSize(Camera.Parameters parameters){
            double scale = ScreenUtil.getScreenWidth(instance)*1.0/ScreenUtil.getScreenHeight(instance);
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            for (Camera.Size size:sizes){
                Log.d("width/height", scale+"="+size.width+"|"+size.height+"="+Math.abs(scale - size.height*1.0/size.width));
                if(Math.abs(scale - size.height*1.0/size.width) < 0.01){
                    parameters.setPictureSize(size.width,size.height);
                    break;
                }
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            try {
                camera = Camera.open();
                camera.setPreviewDisplay(holder);
                camera.setDisplayOrientation(90);
                Camera.Parameters parameters = camera.getParameters();
                parameters.setPictureFormat(ImageFormat.JPEG);
                setOptionSize(parameters);
                parameters.setRotation(90);
                camera.setParameters(parameters);
                camera.startPreview();
                camera.autoFocus(null);
            } catch (Exception e) {
                e.printStackTrace();
                camera = null;
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.release();
            }
        }
    };*/
}
