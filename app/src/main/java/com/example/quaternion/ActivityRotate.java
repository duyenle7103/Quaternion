package com.example.quaternion;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityRotate extends AppCompatActivity {
    private static final String TAG = "ActivityRotate";
    private GLSurfaceView mGLSurfaceView;
    private RendererRotate mRenderer;

    EditText editTextRBOx, editTextRBOy, editTextRBOz,
            editTextRBDx, editTextRBDy, editTextRBDz,
            editTextROOx, editTextROOy, editTextROOz,
            editTextRODx, editTextRODy, editTextRODz,
            editTextRDegree;
    Button buttonRotate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotate);
        mGLSurfaceView = new GLSurfaceView(this);

        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2) {
            Log.d(TAG, "Device supports OpenGL ES 2.0");
            mGLSurfaceView.setEGLContextClientVersion(2);
            mRenderer = new RendererRotate(this);
            mGLSurfaceView.setRenderer(mRenderer);

            editTextRBOx = findViewById(R.id.editTextRBOx);
            editTextRBOy = findViewById(R.id.editTextRBOy);
            editTextRBOz = findViewById(R.id.editTextRBOz);
            editTextRBDx = findViewById(R.id.editTextRBDx);
            editTextRBDy = findViewById(R.id.editTextRBDy);
            editTextRBDz = findViewById(R.id.editTextRBDz);

            editTextROOx = findViewById(R.id.editTextROOx);
            editTextROOy = findViewById(R.id.editTextROOy);
            editTextROOz = findViewById(R.id.editTextROOz);
            editTextRODx = findViewById(R.id.editTextRODx);
            editTextRODy = findViewById(R.id.editTextRODy);
            editTextRODz = findViewById(R.id.editTextRODz);

            editTextRDegree = findViewById(R.id.editTextRDegree);
            buttonRotate = findViewById(R.id.buttonRotate);

            buttonRotate.setOnClickListener(view -> {
                float[] bo = new float[3];
                float[] bd = new float[3];
                float[] oo = new float[3];
                float[] od = new float[3];
                float degree;

                bo[0] = Float.parseFloat(editTextRBOx.getText().toString());
                bo[1] = Float.parseFloat(editTextRBOy.getText().toString());
                bo[2] = Float.parseFloat(editTextRBOz.getText().toString());
                bd[0] = Float.parseFloat(editTextRBDx.getText().toString());
                bd[1] = Float.parseFloat(editTextRBDy.getText().toString());
                bd[2] = Float.parseFloat(editTextRBDz.getText().toString());

                oo[0] = Float.parseFloat(editTextROOx.getText().toString());
                oo[1] = Float.parseFloat(editTextROOy.getText().toString());
                oo[2] = Float.parseFloat(editTextROOz.getText().toString());
                od[0] = Float.parseFloat(editTextRODx.getText().toString());
                od[1] = Float.parseFloat(editTextRODy.getText().toString());
                od[2] = Float.parseFloat(editTextRODz.getText().toString());

                degree = Float.parseFloat(editTextRDegree.getText().toString());

                mRenderer.setRotateInput(bo, bd, oo, od, degree);
                setContentView(mGLSurfaceView);
            });
        } else {
            Log.e(TAG, "Device does not support OpenGL ES 2.0");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }
}