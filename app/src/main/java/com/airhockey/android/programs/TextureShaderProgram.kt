package com.airhockey.android.programs

import android.content.Context
import android.opengl.GLES20.*
import com.airhockey.android.R

class TextureShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {

    // Uniform Locations
    private val uMatrixLocation = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation = glGetUniformLocation(program, U_TEXTURE_UNIT)

    // Attribute Location
    val aPositionLocation = glGetAttribLocation(program, A_POSITION)
    val aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES)


    fun setUniforms(matrix: FloatArray, textureId: Int) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureId)
        // Tell the texture uniform sampler to use this texture in the shader by
        // telling it to read from texture unit 0.
        glUniform1i(uTextureUnitLocation, 0)
    }
}