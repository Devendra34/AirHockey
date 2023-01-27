package com.airhockey.android.util

import kotlin.math.PI
import kotlin.math.tan

object MatrixHelper {


    /**
     * Calculates perspective matrix
     * Parameter a: If we imagine the scene as captured by a camera,
     * then this variable represents the focal length of that camera.
     * The focal length is calculated by 1/tangent of (field of vision/2).
     * The field of vision must be less than 180 degrees.
     * For example, with a field of vision of 90 degrees,
     * the focal length will be set to 1/tangent of (90°/2), which is equal to 1/1, or 1.
     *
     * Perspective matrix: {
     *  {a/[aspect], 0, 0 ,0},
     *  {0,          a, 0 ,0},
     *  {0,          0, -(([f]+[n]) / (([f]-[n])), -((2 * [f] * [n]) / ([f]-[n]))},
     *  {0,          0, -1 ,0},
     * }
     *
     * @param aspect: This should be set to the aspect ratio of the screen,
     * which is equal to width/height.
     * @param f: This should be set to the distance to the far plane and must be positive and greater than the distance to the near plane.
     * @param n: This should be set to the distance to the near plane and must be positive.
     * For example, if this is set to 1, the near plane will be located at a z of -1.
     */
    fun perspectiveM(matrix: FloatArray, yFovInDegrees: Float, aspect: Float, n: Float, f: Float) {

        /**
         * The first thing we’ll do is calculate the focal length,
         * which will be based on the field of vision across the y-axis
         */
        val angleInRadians = yFovInDegrees * PI / 180.0f
        val a = 1f / tan(angleInRadians / 2.0f).toFloat()

        matrix[0] = a / aspect
        matrix[1] = 0f
        matrix[2] = 0f
        matrix[3] = 0f

        matrix[4] = 0f
        matrix[5] = a
        matrix[6] = 0f
        matrix[7] = 0f

        matrix[8] = 0f
        matrix[9] = 0f
        matrix[10] = -(f + n) / (f - n)
        matrix[11] = -(2* f * n) / (f - n)
        matrix[12] = 0f
        matrix[13] = 0f
        matrix[14] = -1f
        matrix[15] = 0f

    }
}