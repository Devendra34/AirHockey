package com.airhockey.android

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import android.util.Log
import com.airhockey.android.util.LoggerConfig
import com.airhockey.android.util.MatrixHelper
import com.airhockey.android.util.ShaderHelper
import com.airhockey.android.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

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
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            +0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
            +0.5f, +0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, +0.8f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

            // Line 1
            -0.5f, 0f, 1.0f, 0.0f, 0.0f,
            +0.5f, 0f, 1.0f, 0.0f, 0.0f,

            // Mallets
            0.0f, -0.4f, 0.0f, 0.0f, 1.0f,
            0.0f, +0.4f, 1.0f, 0.0f, 0.0f,
        )
    }

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

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
        glClearColor(0.3f, 0.2f, 0.8f, 0.0f)
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

        val aspectRatio = width / height.toFloat()
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -2f)
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f)
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspectRatio, 1f, 10f)

        val mvpMatrix = FloatArray(16)
        multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mvpMatrix, 0)
    }

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
        glDrawArrays(GL_LINES, 6, 2)
        glDrawArrays(GL_POINTS, 8, 1)
        glDrawArrays(GL_POINTS, 9, 1)
    }

}