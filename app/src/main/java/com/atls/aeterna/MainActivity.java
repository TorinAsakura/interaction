package com.atls.aeterna;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "interaction";
    public static int OVERLAY_PERMISSION_REQ_CODE = 1;

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermissionOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(MainActivity.this, "Overlay permission required!", Toast.LENGTH_LONG).show();
            Intent intentSettings = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intentSettings, OVERLAY_PERMISSION_REQ_CODE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        checkPermissionOverlay();
        Intent svc = new Intent(this, OverlayService.class);
        startService(svc);
        finish();
    }
}