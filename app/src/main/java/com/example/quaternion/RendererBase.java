package com.example.quaternion;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class RendererBase implements GLSurfaceView.Renderer {
    protected Context context;
    public float[] mViewMatrix = new float[16];
    public float[] mProjectionMatrix = new float[16];
    public int mPositionHandle;
    public int mColorHandle;
    public int mProgramHandle;
    public int mMVPMatrixHandle;

    public RendererBase(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }
}
