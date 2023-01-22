package com.airhockey.android

import android.app.ActivityManager
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class MainActivity : AppCompatActivity() {

    private var glSurfaceView: GLSurfaceView? = null
    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)
        val activityManager = getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        val configurationInfo = activityManager?.deviceConfigurationInfo

        val deviceGlEsVersion = configurationInfo?.reqGlEsVersion
        val supportEs2 = deviceGlEsVersion?.let { it >= 0x20000
                || (Build.FINGERPRINT.startsWith("generic"))
                || (Build.FINGERPRINT.startsWith("unknown"))
                || (Build.MODEL.contains("google_sdk"))
                || (Build.MODEL.contains("Emulator"))
                || (Build.MODEL.contains("Android SDK built for x86"))
        } ?: false
        if (supportEs2) {
            glSurfaceView?.setEGLContextClientVersion(2)
            glSurfaceView?.setRenderer(AirHockeyRenderer(this))
            rendererSet = true
        } else {
            Toast.makeText(this, "Device does not supports OpenGL ES 2.0\n$deviceGlEsVersion", Toast.LENGTH_LONG).show()
            return
        }
        setContentView(glSurfaceView)
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            glSurfaceView?.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            glSurfaceView?.onResume()
        }
    }
}