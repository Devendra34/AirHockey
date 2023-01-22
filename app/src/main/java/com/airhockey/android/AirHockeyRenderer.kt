package com.airhockey.android

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.orthoM
import android.util.Log
import com.airhockey.android.util.LoggerConfig
import com.airhockey.android.util.ShaderHelper
import com.airhockey.android.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min

class AirHockeyRenderer(private val context: Context) : Renderer {

    companion object {
        private const val TAG = "AirHockeyRenderer"
        private const val BYTES_PER_FLOAT = 4
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
        private const val A_POSITION = "a_Position"
        private const val A_COLOR = "a_Color"
        private const val U_MATRIX = "u_Matrix"

        val tableVerticesWithTriangles = floatArrayOf(
            // Triangle 1
            +0.0f, +0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, -0.8f, 0.0f, 0.0f, 0.7f,
            +0.5f, -0.8f, 0.0f, 0.0f, 0.7f,
            +0.5f, +0.8f, 0.0f, 0.0f, 0.7f,
            -0.5f, +0.8f, 0.0f, 0.0f, 0.7f,
            -0.5f, -0.8f, 0.0f, 0.0f, 0.7f,

            // Line 1
            -0.5f, 0f, 1.0f, 0.0f, 0.0f,
            +0.5f, 0f, 1.0f, 0.0f, 0.0f,

            // Mallets
            0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
            0.0f, +0.4f, 1.0f, 0.0f, 0.0f,
        )
    }

    private val projectionMatrix = FloatArray(16)

    private var program: Int = 0
    private var aPositionLocation = 0
    private var aColorLocation = 0
    private var uMatrixLocation = 0

    private val vertexData: FloatBuffer =
        ByteBuffer.allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer().also {
                it.put(tableVerticesWithTriangles)
            }


    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader)
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }

        glUseProgram(program)
        aPositionLocation = glGetAttribLocation(program, A_POSITION)
        aColorLocation = glGetAttribLocation(program, A_COLOR)
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX)


        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            COLOR_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        glEnableVertexAttribArray(aColorLocation)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        if (LoggerConfig.ON) {
            Log.w(TAG, "onSurfaceChanged: size = ($width, $height)")
        }
        glViewport(0, 0, width, height)

        val aspectRatio = max(width, height).toFloat() / min(width, height).toFloat()
        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, -1f, 1f)
        } else {
            // Portrait
            orthoM(projectionMatrix, 0, -1f, 1f, -aspectRatio, aspectRatio, -1f, 1f)
        }
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0)
    }

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        glDrawArrays(GL_LINES, 6, 2)
        glDrawArrays(GL_POINTS, 8, 1)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}