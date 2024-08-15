package com.example.quaternion;

public class Quaternion {
    private static final String TAG = "Quaternion";
    private static final int mQuaternionSize = 4;
    public float w, x, y, z;

    public Quaternion() {
        this.w = 1;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion add(Quaternion q) {
        return new Quaternion(
                this.w + q.w,
                this.x + q.x,
                this.y + q.y,
                this.z + q.z
        );
    }

    public Quaternion subtract(Quaternion q) {
        return new Quaternion(
                this.w - q.w,
                this.x - q.x,
                this.y - q.y,
                this.z - q.z
        );
    }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(
                this.w * q.w - this.x * q.x - this.y * q.y - this.z * q.z,
                this.w * q.x + this.x * q.w + this.y * q.z - this.z * q.y,
                this.w * q.y - this.x * q.z + this.y * q.w + this.z * q.x,
                this.w * q.z + this.x * q.y - this.y * q.x + this.z * q.w
        );
    }

    public Quaternion conjugate() {
        return new Quaternion(this.w, -this.x, -this.y, -this.z);
    }

    public float magnitude() {
        return (float) Math.sqrt(
                this.w * this.w +
                this.x * this.x +
                this.y * this.y +
                this.z * this.z
        );
    }

    public Quaternion inverse() {
        float len = this.magnitude();
        float lenSq = len * len;
        return new Quaternion(
                this.w / lenSq,
                -this.x / lenSq,
                -this.y / lenSq,
                -this.z / lenSq
        );
    }

    public Quaternion normalize() {
        float len = this.magnitude();
        return new Quaternion(this.w / len, this.x / len, this.y / len, this.z / len);
    }

    public float[] rotate(float vx, float vy, float vz) {
        Quaternion p = new Quaternion(0, vx, vy, vz);
        Quaternion qInverse = this.inverse();
        Quaternion result = this.multiply(p).multiply(qInverse);
        return new float[]{result.x, result.y, result.z};
    }
}