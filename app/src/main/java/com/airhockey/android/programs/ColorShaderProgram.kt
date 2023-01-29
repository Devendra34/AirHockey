package com.airhockey.android.programs

import android.content.Context
import android.opengl.GLES20.*
import com.airhockey.android.R

class ColorShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader
) {

    // Uniform Locations
    private val uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
    private val uColorLocation = glGetUniformLocation(program, U_COLOR)

    // Attribute Location
    val aPositionLocation = glGetAttribLocation(program, A_POSITION)

    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float, a: Float = 1f) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform4f(uColorLocation, r, g, b, a)
    }
}