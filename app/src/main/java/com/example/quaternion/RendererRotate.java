package com.example.quaternion;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class RendererRotate extends RendererBase {
    private static final String TAG = "RendererRotate";
    private static final float[] eye = {0.0f, 0.0f, 1.5f};
    private static final float[] look = {0.0f, 0.0f, -5.0f};
    private static final float[] up = {0.0f, 1.0f, 0.0f};
    private Utils utils;
    private Quaternion quaternCurr, quaternEnd;
    private Ray rayBase, rayObject, rayCurr, rayEnd;
    private float[] vecBase, vecQuatern;
    private float currentDegree, targetDegree;
    private float rotationProgress = 0.0f;
    private float rotationSpeed = 0.1f;

    public RendererRotate(Context context) {
        super(context);

        Log.d(TAG, "RendererRotate called");
        utils = new Utils();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated called");

        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        Matrix.setLookAtM(mViewMatrix, 0, eye[0], eye[1], eye[2],
                        look[0], look[1], look[2], up[0], up[1], up[2]);

        final int vertexShaderHandle = utils.loadVertexShader();
        final int fragmentShaderHandle = utils.loadFragmentShader();

        mProgramHandle = utils.createProgram(vertexShaderHandle, fragmentShaderHandle);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "a_Color");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        GLES20.glUseProgram(mProgramHandle);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged called");

        GLES20.glViewport(0, 0, width, height);
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 0.6f;
        final float far = 10.0f;

        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        Log.d(TAG, "onDrawFrame called");

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        rayBase.setModelMatrix(null, null, null);
        rayBase.draw(this, utils.WHITE);
        rayObject.setModelMatrix(null, null, null);
        rayObject.draw(this, utils.RED);
        rayEnd.setModelMatrix(null, null, null);
        rayEnd.draw(this, utils.GREEN);
    }

    public void setRotateInput(float[] baseOrigin, float[] baseDirection,
                               float[] objectOrigin, float[] objectDirection, float degree) {
        Log.d(TAG, "setRotateInput called");
        
        Log.d(TAG, Arrays.toString(baseOrigin));
        Log.d(TAG, Arrays.toString(baseDirection));
        Log.d(TAG, Arrays.toString(objectOrigin));
        Log.d(TAG, Arrays.toString(objectDirection));
        Log.d(TAG, String.valueOf(degree));

        vecBase = utils.addVector(baseOrigin, baseDirection);
        targetDegree = degree;

        Point baseStart = new Point(baseOrigin);
        Point baseDirect = new Point(baseDirection);
        rayBase = new Ray(baseStart, baseDirect);

        Point objectStart = new Point(objectOrigin);
        Point objectDirect = new Point(objectDirection);
        rayObject = new Ray(objectStart, objectDirect);

        vecQuatern = utils.normalizeVector(baseDirection);
        quaternEnd = utils.fromAxisAngle(vecQuatern, targetDegree);

        Log.d(TAG, Arrays.toString(vecQuatern));
        Log.d(TAG, quaternEnd.w + " " + quaternEnd.x + " " + quaternEnd.y + " " + quaternEnd.z);

        rayEnd = rayObject.rotate(quaternEnd);
    }
}