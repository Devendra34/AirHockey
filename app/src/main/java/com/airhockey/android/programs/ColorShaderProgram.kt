package com.airhockey.android.programs

import android.content.Context
import android.opengl.GLES20.*
import com.airhockey.android.R

class ColorShaderProgram(context: Context) : ShaderProgram(
    context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader
) {

    // Uniform Locations
    private val uMatrixLocation = glGetUniformLocation(program, U_MATRIX)

    // Attribute Location
    val aPositionLocation = glGetAttribLocation(program, A_POSITION)
    val aColorLocation = glGetAttribLocation(program, A_COLOR)

    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }
}