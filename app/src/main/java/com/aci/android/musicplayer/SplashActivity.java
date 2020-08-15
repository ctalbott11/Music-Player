package com.aci.android.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.aci.android.musicplayer.databinding.ActivitySplashScreenBinding;

import java.util.Objects;

public class SplashActivity extends Activity {

    private static final int SPLASH_DISPLAY_TIME = 1000;
    public static boolean permissionToWriteStorage = false;
    private final String[] permissions = { android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_splash_screen);

        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
            Intent intent = new Intent();
            intent.setClass(SplashActivity.this,
                    MainActivity.class);

            SplashActivity.this.startActivity(intent);
            SplashActivity.this.finish();

        }, SPLASH_DISPLAY_TIME);

        /*if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.e("testing", "Permission is granted");
        }
        this.requestPermissions(permissions, 200);*/
    }
    @Override
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionToWriteStorage = true;
            } else {
                //permission denied
                finish();
            }
        }
        if (!permissionToWriteStorage) finish();
    }
}