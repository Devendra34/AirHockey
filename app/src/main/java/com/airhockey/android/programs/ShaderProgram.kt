package com.airhockey.android.programs

import android.content.Context
import android.opengl.GLES20.glUseProgram
import com.airhockey.android.util.ShaderHelper
import com.airhockey.android.util.TextResourceReader

open class ShaderProgram(
    context: Context,
    vertexShaderResourceId: Int,
    fragmentShaderResourceId: Int
) {

    companion object {
        // Uniform Constants
        @JvmStatic
        protected val U_MATRIX = "u_Matrix"

        @JvmStatic
        protected val U_COLOR = "u_Color"

        @JvmStatic
        protected val U_TEXTURE_UNIT = "u_TextureUnit"


        // Attribute Constants
        @JvmStatic
        protected val A_POSITION = "a_Position"

        @JvmStatic
        protected val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }

    // shaderProgram
    protected val program = ShaderHelper.buildProgram(
        TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId),
        TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId),
    )

    fun useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program)
    }
}