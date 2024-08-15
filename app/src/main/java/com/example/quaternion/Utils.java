package com.example.quaternion;

import static android.opengl.GLES20.glCreateShader;

import android.opengl.GLES20;

public class Utils {
    public static final String TAG = "Utils";
    protected final float[] RED = new float[]{1, 0, 0, 1};
    protected final float[] YELLOW = new float[]{1, 1, 0, 1};
    protected final float[] GREEN = new float[]{0, 1, 0, 1};
    protected final float[] BLUE = new float[]{0, 0, 1, 1};
    protected final float[] WHITE = new float[]{1, 1, 1, 1};

    private String getVertexShader() {
        return
                "uniform mat4 u_MVPMatrix;          \n"
                + "attribute vec4 a_Position;       \n"
                + "uniform vec4 a_Color;            \n"
                + "varying vec4 v_Color;            \n"

                + "void main()                      \n"
                + "{                                \n"
                + "   v_Color = a_Color;            \n"
                + "   gl_PointSize = 5.0;           \n"
                + "   gl_Position = u_MVPMatrix     \n"
                + "               * a_Position;     \n"
                + "}                                \n";
    }

    public int loadVertexShader() {
        String vertexShader = getVertexShader();
        int vertexShaderHandle = glCreateShader(GLES20.GL_VERTEX_SHADER);
        if (vertexShaderHandle != 0) {
            GLES20.glShaderSource(vertexShaderHandle, vertexShader);
            GLES20.glCompileShader(vertexShaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(vertexShaderHandle);
                vertexShaderHandle = 0;
            }
        }
        if (vertexShaderHandle == 0) {
            throw new RuntimeException("Error creating vertex shader.");
        }
        return vertexShaderHandle;
    }

    private String getFragmentShader() {
        return
                "precision mediump float;           \n"
                + "varying vec4 v_Color;            \n"

                + "void main()                      \n"
                + "{                                \n"
                + "   gl_FragColor = v_Color;       \n"
                + "}                                \n";
    }

    public int loadFragmentShader() {
        String fragmentShader = getFragmentShader();
        int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        if (fragmentShaderHandle != 0) {
            GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
            GLES20.glCompileShader(fragmentShaderHandle);

            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0) {
                GLES20.glDeleteShader(fragmentShaderHandle);
                fragmentShaderHandle = 0;
            }
        }
        if (fragmentShaderHandle == 0) {
            throw new RuntimeException("Error creating fragment shader.");
        }
        return fragmentShaderHandle;
    }

    public int createProgram(int vertexShaderHandle, int fragmentShaderHandle) {
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
            GLES20.glLinkProgram(programHandle);

            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }
        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return programHandle;
    }

    public float[] addVector(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new RuntimeException("Different vector length in addition calculation");
        }
        int numElements = v1.length;
        float[] result = new float[numElements];
        for (int i = 0; i < numElements; i++) {
            result[i] = v1[i] + v2[i];
        }
        return result;
    }

    public float[] subtractVector(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new RuntimeException("Different vector length in subtraction calculation");
        }
        int numElements = v1.length;
        float[] result = new float[numElements];
        for (int i = 0; i < numElements; i++) {
            result[i] = v1[i] - v2[i];
        }
        return result;
    }

    public float dotProductVector(float[] v1, float[] v2) {
        if (v1.length != v2.length) {
            throw new RuntimeException("Different vector length in dot product calculation");
        }
        int numElements = v1.length;
        float result = 0;
        for (int i = 0; i < numElements; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    private float[] crossProductVector3Dimension(float[] v1, float[] v2) {
        float[] result = new float[3];
        result[0] = v1[1] * v2[2] - v1[2] * v2[1];
        result[1] = v1[2] * v2[0] - v1[0] * v2[2];
        result[2] = v1[0] * v2[1] - v1[1] * v2[0];
        return result;
    }

    private float[][] removeRowAndColumn(float[][] matrix, int row, int col) {
        int n = matrix.length;
        float[][] result = new float[n - 1][n - 1];
        int r = -1;
        for (int i = 0; i < n; i++) {
            if (i == row) continue;
            r++;
            int c = -1;
            for (int j = 0; j < n; j++) {
                if (j == col) continue;
                c++;
                result[r][c] = matrix[i][j];
            }
        }
        return result;
    }

    private float determinantOfMatrix(float[][] matrix) {
        int n = matrix.length;
        if (n == 1) {
            return matrix[0][0];
        }
        if (n == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        }
        float det = 0f;
        for (int i = 0; i < n; i++) {
            det += (float) Math.pow(-1, i) * matrix[0][i]
                    * determinantOfMatrix(removeRowAndColumn(matrix, 0, i));
        }
        return det;
    }

    private float[] crossProductVectorAnyDimension(float[]... vectors) {
        // Initialize the matrix
        int dimension = vectors[0].length;
        float[][] matrix = new float[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == 0) {
                    matrix[i][j] = 1; // The first row is always 1
                } else {
                    matrix[i][j] = vectors[j][i - 1];
                }
            }
        }

        // Calculate the determinant of the matrix
        float[] result = new float[dimension];
        for (int i = 0; i < dimension; i++) {
            result[i] = determinantOfMatrix(removeRowAndColumn(matrix, 0, i));
        }

        return result;
    }

    public float[] crossProductVector(float[]... vectors) {
        if (vectors.length < 2) {
            throw new RuntimeException("Cross product requires at least 2 vectors");
        }

        int dimension = vectors[0].length;
        for (float[] vector : vectors) {
            if (vector.length != dimension) {
                throw new RuntimeException("All vectors must have the same dimension.");
            }
        }

        if (dimension == 3) {
            return crossProductVector3Dimension(vectors[0], vectors[1]);
        } else {
            return crossProductVectorAnyDimension(vectors);
        }
    }

    public float magnitudeVector(float[] v) {
        float sum = 0;
        for (float value : v) {
            sum += value * value;
        }
        return (float) Math.sqrt(sum);
    }

    public float[] normalizeVector(float[] v) {
        float magnitude = magnitudeVector(v);
        int numElements = v.length;
        float[] result = new float[numElements];
        for (int i = 0; i < numElements; i++) {
            result[i] = v[i] / magnitude;
        }
        return result;
    }

    public Quaternion fromAxisAngle(float[] axis, float angle) {
        float radian = (float) Math.toRadians(angle);
        float halfAngle = radian / 2.0f;
        float cosHalfAngle = (float) Math.cos(halfAngle);
        float sinHalfAngle = (float) Math.sin(halfAngle);
        return new Quaternion(
                cosHalfAngle,
                axis[0] * sinHalfAngle,
                axis[1] * sinHalfAngle,
                axis[2] * sinHalfAngle
        );
    }
}