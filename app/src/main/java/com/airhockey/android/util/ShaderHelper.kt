package com.airhockey.android.util

import android.opengl.GLES20.*
import android.util.Log

object ShaderHelper {

    private const val TAG = "ShaderHelper"

    fun compileVertexShader(shaderSource: String): Int {
        return compileShader(GL_VERTEX_SHADER, shaderSource)
    }

    fun compileFragmentShader(shaderSource: String): Int {
        return compileShader(GL_FRAGMENT_SHADER, shaderSource)
    }

    fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
        val programObjectId = glCreateProgram()
        if (programObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "linkProgram: Could not create new Program")
            }
            return 0
        }
        if (LoggerConfig.ON) {
            Log.w(TAG, "linkProgram: vertexShaderId=$vertexShaderId, fragmentShaderId=$fragmentShaderId")
        }
        glAttachShader(programObjectId, vertexShaderId)
        glAttachShader(programObjectId, fragmentShaderId)
        glLinkProgram(programObjectId)

        val linkStatus = intArrayOf(0)
        glGetProgramiv(programObjectId, GL_LINK_STATUS, linkStatus, 0)
        if (LoggerConfig.ON) {
            val result = glGetProgramInfoLog(programObjectId)
            Log.v(TAG, "linkProgram: Result of linking program: $result")
        }
        if (linkStatus[0] == 0) {
            glDeleteProgram(programObjectId)
            if (LoggerConfig.ON) {
                Log.w(TAG, "linkProgram: Linking of program failed.")
            }
        }
        return programObjectId
    }

    fun validateProgram(programObjectId: Int): Boolean {
        glValidateProgram(programObjectId)
        val validationStatus = intArrayOf(0)
        glGetProgramiv(programObjectId, GL_VALIDATE_STATUS, validationStatus, 0)
        val result = glGetProgramInfoLog(programObjectId)
        Log.w(TAG, "validateProgram: Results of validating program: ${validationStatus[0]}, $result")
        return validationStatus[0] != 0
    }

    private fun compileShader(type: Int, shaderSource: String): Int {
        val shaderObjectId = glCreateShader(type)
        if (shaderObjectId == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "compileShader: Could not create new Shader")
            }
            return 0
        }
        glShaderSource(shaderObjectId, shaderSource)
        glCompileShader(shaderObjectId)
        val compileStatus = intArrayOf(0)
        glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
        if (LoggerConfig.ON) {
            val result = glGetShaderInfoLog(shaderObjectId)
            Log.v(TAG, "compileShader: Result of compiling source: \n$shaderSource \n$result")
        }
        if (compileStatus[0] == 0) {
            glDeleteShader(shaderObjectId)
            if (LoggerConfig.ON) {
                Log.w(TAG, "compileShader: Compilation of shader failed.")
            }
        }
        return shaderObjectId
    }
}