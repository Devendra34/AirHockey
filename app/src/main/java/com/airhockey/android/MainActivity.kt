package com.airhockey.android

import android.app.ActivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.airhockey.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var airHockeyRenderer: AirHockeyRenderer? = null
    private var rendererSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initOpenGl()
    }

    override fun onPause() {
        super.onPause()
        if (rendererSet) {
            binding.glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet) {
            binding.glSurfaceView.onResume()
        }
    }

    private fun initOpenGl() {
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
            airHockeyRenderer = AirHockeyRenderer(this)
            binding.glSurfaceView.setEGLContextClientVersion(2)
            binding.glSurfaceView.setRenderer(airHockeyRenderer)
            rendererSet = true
        } else {
            Toast.makeText(this, "Device does not supports OpenGL ES 2.0\n$deviceGlEsVersion", Toast.LENGTH_LONG).show()
        }
    }
}