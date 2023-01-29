package com.airhockey.android.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log

object TextureHelper {

    private const val TAG = "TextureHelper"

    fun loadTexture(context: Context, resourceId: Int): Int {
        // get original form of image without any scaling applied
        val bitmapOptions = BitmapFactory.Options().apply { inScaled = false }
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, bitmapOptions)
        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID $resourceId could not be decoded.")
            }
            return 0
        }
        return loadTexture(bitmap, true)
    }

    fun loadTexture(bitmap: Bitmap, shouldRecycle: Boolean): Int {
        val textureObjectIds = IntArray(1)

        // generate textures
        glGenTextures(textureObjectIds.size, textureObjectIds, 0)
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")
            }
            return 0
        }

        // bind textures
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])

        // apply texture filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        // load bitmap into texture and recycle if not required anymore
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        if (shouldRecycle) {
            bitmap.recycle()
        }

        // generate mipmaps from opengl
        glGenerateMipmap(GL_TEXTURE_2D)

        // bitmap is loaded. unbind texture to avoid future accidental modifications
        glBindTexture(GL_TEXTURE_2D, 0)
        return textureObjectIds[0]
    }
}