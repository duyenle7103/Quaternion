package com.example.quaternion;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class Obj {
    protected float[] mModelMatrix = new float[16];
    protected float[] mMVMatrix = new float[16];
    protected float[] mMVPMatrix = new float[16];
    protected float[] mInvTransMatrix = new float[16];
    protected final int mBytesPerFloat = 4;
    protected final int mPositionOffset = 0;
    protected final int mPositionDataSize = 3;
    protected final int mColorDataSize = 4;
    protected final int mStrideBytes = mPositionDataSize * mBytesPerFloat;
    protected boolean modelMatrixUpdated = false;

    public void setModelMatrix(float[] translate, float[] rotate, float[] scale) {
        if (!modelMatrixUpdated) {
            Matrix.setIdentityM(mModelMatrix, 0);
            if (scale != null) {
                Matrix.scaleM(mModelMatrix, 0, scale[0], scale[1], scale[2]);
            }
            if (translate != null) {
                Matrix.translateM(mModelMatrix, 0, translate[0], translate[1], translate[2]);
            }
            if (rotate != null) {
                Matrix.rotateM(mModelMatrix, 0, rotate[0], rotate[1], rotate[2], rotate[3]);
            }
            modelMatrixUpdated = true;
        }
    }

    public abstract void draw(RendererBase renderer, float[] color);
}

class Point extends Obj {
    private static final String TAG = "Point";
    public float x, y, z;
    FloatBuffer pointsBuffer;

    public Point(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        pointsBuffer = ByteBuffer.allocateDirect(mPositionDataSize * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pointsBuffer.put(new float[]{this.x, this.y, this.z});
        pointsBuffer.position(mPositionOffset);
    }

    public Point(float[] coord) {
        if (coord.length != 3) {
            throw new RuntimeException("Invalid point coordinate");
        }
        this.x = coord[0];
        this.y = coord[1];
        this.z = coord[2];
        pointsBuffer = ByteBuffer.allocateDirect(mPositionDataSize * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        pointsBuffer.put(new float[]{this.x, this.y, this.z});
        pointsBuffer.position(mPositionOffset);
    }

    public float distanceTo(Point other) {
        return (float) Math.sqrt(
                (this.x - other.x) * (this.x - other.x) +
                (this.y - other.y) * (this.y - other.y) +
                (this.z - other.z) * (this.z - other.z)
        );
    }

    public Point rotate(Quaternion q) {
        float[] newPoint = q.rotate(this.x, this.y, this.z);
        return new Point(newPoint);
    }

    @Override
    public void draw(RendererBase renderer, float[] color) {
        Log.d(TAG, "Draw point: (" + this.x + "," + this.y + "," + this.z + ")");

        if (color == null) {
            color = new float[]{1, 1, 1, 1};
        }
        if (color.length != mColorDataSize) {
            throw new RuntimeException("Invalid color");
        }

        pointsBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(renderer.mPositionHandle);
        GLES20.glVertexAttribPointer(renderer.mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, pointsBuffer);

        Matrix.multiplyMM(mMVMatrix, 0, renderer.mViewMatrix, 0, mModelMatrix, 0);
        Matrix.invertM(mInvTransMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mInvTransMatrix, 0, mInvTransMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, renderer.mProjectionMatrix, 0, mMVMatrix, 0);

        GLES20.glUniformMatrix4fv(renderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform4fv(renderer.mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}

class Line extends Obj {
    private static final String TAG = "Line";
    public Point point1, point2;
    FloatBuffer lineBuffer;

    public Line(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
        lineBuffer = ByteBuffer.allocateDirect(2 * mPositionDataSize * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        lineBuffer.put(new float[]{this.point1.x, this.point1.y, this.point1.z,
                this.point2.x, this.point2.y, this.point2.z});
        lineBuffer.position(mPositionOffset);
    }

    public Line rotate(Quaternion q) {
        Point newPoint1 = point1.rotate(q);
        Point newPoint2 = point2.rotate(q);
        return new Line(newPoint1, newPoint2);
    }

    @Override
    public void draw(RendererBase renderer, float[] color) {
        Log.d(TAG, "Draw line: (" + this.point1.x + "," + this.point1.y + "," + this.point1.z + "),(" +
                                         this.point2.x + "," + this.point2.y + "," + this.point2.z + ")");

        if (color == null) {
            color = new float[]{1, 1, 1, 1};
        }
        if (color.length != mColorDataSize) {
            throw new RuntimeException("Invalid color");
        }

        lineBuffer.position(mPositionOffset);
        GLES20.glEnableVertexAttribArray(renderer.mPositionHandle);
        GLES20.glVertexAttribPointer(renderer.mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, lineBuffer);

        Matrix.multiplyMM(mMVMatrix, 0, renderer.mViewMatrix, 0, mModelMatrix, 0);
        Matrix.invertM(mInvTransMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mInvTransMatrix, 0, mInvTransMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, renderer.mProjectionMatrix, 0, mMVMatrix, 0);

        GLES20.glUniformMatrix4fv(renderer.mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform4fv(renderer.mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
    }
}

class Ray extends Obj {
    private static final String TAG = "Ray";
    public Point origin, direction;
    FloatBuffer rayBuffer;

    public Ray(Point origin, Point direction) {
        Log.d(TAG, "Origin: (" + origin.x + "," + origin.y + "," + origin.z + ")");
        Log.d(TAG, "Direction: (" + direction.x + "," + direction.y + "," + direction.z + ")");

        this.origin = origin;
        this.direction = normalize(direction);

        Log.d(TAG, "Create ray: (" + this.origin.x + "," + this.origin.y + "," + this.origin.z + "),(" +
                this.direction.x + "," + this.direction.y + "," + this.direction.z + ")");
    }

    private Point normalize(Point direct) {
        float length = (float) Math.sqrt(direct.x * direct.x +
                                         direct.y * direct.y +
                                         direct.z * direct.z);
        return new Point(direct.x / length, direct.y / length, direct.z / length);
    }

    public Ray rotate(Quaternion q) {
        Point newOrigin = origin.rotate(q);
        Point newDirection = direction.rotate(q);
        return new Ray(newOrigin, newDirection);
    }

    @Override
    public void draw(RendererBase renderer, float[] color) {
        Log.d(TAG, "Draw ray: (" + this.origin.x + "," + this.origin.y + "," + this.origin.z + "),(" +
                                        this.direction.x + "," + this.direction.y + "," + this.direction.z + ")");

        if (color == null) {
            color = new float[]{1, 1, 1, 1};
        }
        if (color.length != mColorDataSize) {
            throw new RuntimeException("Invalid color");
        }

        Point end = new Point(origin.x + direction.x * 100,
                              origin.y + direction.y * 100,
                              origin.z + direction.z * 100);
        Line line = new Line(origin, end);

        origin.setModelMatrix(null, null, null);
        origin.draw(renderer, color);
        line.setModelMatrix(null, null, null);
        line.draw(renderer, color);
    }
}