package com.airhockey.android

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView.Renderer
import android.opengl.Matrix.*
import android.util.Log
import com.airhockey.android.objects.Mallet
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
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureShaderProgram: TextureShaderProgram
    private lateinit var colorShaderProgram: ColorShaderProgram

    private var texture = 0

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        table = Table()
        mallet = Mallet()

        textureShaderProgram = TextureShaderProgram(context)
        colorShaderProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        if (LoggerConfig.ON) {
            Log.w(TAG, "onSurfaceChanged: size = ($width, $height)")
        }
        glViewport(0, 0, width, height)

        val aspectRatio = width / height.toFloat()
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, 0f, 0f, -1.5f)
        rotateM(modelMatrix, 0, -30f, 1f, 0f, 0f)
        MatrixHelper.perspectiveM(projectionMatrix, 45f, aspectRatio, 1f, 10f)

        val mvpMatrix = FloatArray(16)
        multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix.size)

        textureShaderProgram.useProgram()
        textureShaderProgram.setUniforms(mvpMatrix, texture)

        colorShaderProgram.useProgram()
        colorShaderProgram.setUniforms(mvpMatrix)
    }

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        textureShaderProgram.useProgram()
        table.bindData(textureShaderProgram)
        table.draw()

        colorShaderProgram.useProgram()
        mallet.bindData(colorShaderProgram)
        mallet.draw()
    }

}