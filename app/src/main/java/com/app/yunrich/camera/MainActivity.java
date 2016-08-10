package com.app.yunrich.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
}
