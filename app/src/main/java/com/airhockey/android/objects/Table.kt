package com.airhockey.android.objects

import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays
import com.airhockey.android.Constants.BYTES_PER_FLOAT
import com.airhockey.android.data.VertexArray
import com.airhockey.android.programs.TextureShaderProgram

class Table : DrawObject<TextureShaderProgram> {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, S, T
            +0.0f, -0.0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0.0f, 0.9f,
            +0.5f, -0.8f, 1.0f, 0.9f,
            +0.5f, +0.8f, 1.0f, 0.1f,
            -0.5f, +0.8f, 0.0f, 0.1f,
            -0.5f, -0.8f, 0.0f, 0.9f,
        )
    }

    private val vertexArray = VertexArray(VERTEX_DATA)

    override fun bindData(shaderProgram: TextureShaderProgram) {
        // position
        vertexArray.setVertexAttribPointer(
            0,
            shaderProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        // texture coordinates
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            shaderProgram.aTextureCoordinatesLocation,
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    override fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }
}