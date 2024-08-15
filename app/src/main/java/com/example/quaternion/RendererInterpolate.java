package com.example.quaternion;

import android.content.Context;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererInterpolate extends RendererBase {
    public RendererInterpolate(Context context) {
        super(context);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Drawing code for RendererB
    }
}