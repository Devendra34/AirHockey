package com.airhockey.android.objects

import com.airhockey.android.programs.ShaderProgram

interface DrawObject<P : ShaderProgram> {

    fun bindData(shaderProgram: P)

    fun draw()
}