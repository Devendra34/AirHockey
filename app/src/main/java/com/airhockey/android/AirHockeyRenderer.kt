package com.airhockey.android

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import android.util.Log
import com.airhockey.android.objects.Mallet
import com.airhockey.android.objects.Puck
import com.airhockey.android.objects.Table
import com.airhockey.android.programs.ColorShaderProgram
import com.airhockey.android.programs.TextureShaderProgram
import com.airhockey.android.util.LoggerConfig
import com.airhockey.android.util.MatrixHelper
import com.airhockey.android.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : Renderer {

    companion object {
        private const val TAG = "AirHockeyRenderer"
    }

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private val eyePosition = floatArrayOf(0f, 1.2f, 1.2f)
    private val lookAtPosition = floatArrayOf(0f, 0f, 0f)
    private val upPosition = floatArrayOf(0f, 1f, 0f)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureShaderProgram: TextureShaderProgram
    private lateinit var colorShaderProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        textureShaderProgram = TextureShaderProgram(context)
        colorShaderProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
        updateViewMatrix()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        if (LoggerConfig.ON) {
            Log.w(TAG, "onSurfaceChanged: size = ($width, $height)")
        }
        glViewport(0, 0, width, height)

        val aspectRatio = width / height.toFloat()
        updateProjectionMatrix(aspectRatio)
        updateViewProjectionMatrix()
    }

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        // Draw table
        positionTableInScene()
        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureShaderProgram)
        table.draw()

        // Draw mallets
        positionObjectInScene(floatArrayOf(0f, mallet.height / 2f, -0.4f))
        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorShaderProgram)
        mallet.draw()

        positionObjectInScene(floatArrayOf(0f, mallet.height / 2f, 0.4f))
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.bindData(colorShaderProgram)
        mallet.draw()

        // Draw puck
        positionObjectInScene(floatArrayOf(0f, puck.height / 2f, 0f))
        colorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorShaderProgram)
        puck.draw()
    }

    private fun positionTableInScene() {
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        updateModelViewProjectionMatrix()
    }

    private fun positionObjectInScene(position: FloatArray) {
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, position[0], position[1], position[2])
        updateModelViewProjectionMatrix()
    }

    private fun updateProjectionMatrix(aspectRatio: Float, near: Float = 1f, far: Float = 10f) {
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspectRatio, near, far)
    }

    private fun updateViewMatrix() {
        val offset = 0
        setLookAtM(
            viewMatrix, offset,
            eyePosition[0], eyePosition[1], eyePosition[2],
            lookAtPosition[0], lookAtPosition[1], lookAtPosition[2],
            upPosition[0], upPosition[1], upPosition[2],
        )
    }

    private fun updateViewProjectionMatrix() {
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    private fun updateModelViewProjectionMatrix() {
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

}