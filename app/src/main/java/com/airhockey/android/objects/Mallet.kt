package com.airhockey.android.objects

import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.airhockey.android.Constants.BYTES_PER_FLOAT
import com.airhockey.android.data.VertexArray
import com.airhockey.android.programs.ColorShaderProgram

class Mallet {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

        private val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, R, G, B
            0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
            0.0f, +0.4f, 1.0f, 0.0f, 0.0f,
        )
    }

    private val vertexArray = VertexArray(VERTEX_DATA)

    fun bindData(colorShaderProgram: ColorShaderProgram) {
        // position
        vertexArray.setVertexAttribPointer(
            0,
            colorShaderProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )

        // color
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorShaderProgram.aColorLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_POINTS, 0, 2)
    }
}