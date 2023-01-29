package com.airhockey.android.objects

import com.airhockey.android.data.VertexArray
import com.airhockey.android.programs.ColorShaderProgram
import com.airhockey.android.util.Geometry.Cylinder
import com.airhockey.android.util.Geometry.Point

class Puck(
    radius: Float,
    val height: Float,
    numPointsAroundPuck: Int,
) : DrawObject<ColorShaderProgram> {
    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }

    private val generatedData = ObjectBuilder.createPuck(
        Cylinder(Point(0f, 0f, 0f), radius, height), numPointsAroundPuck
    )
    private val vertexArray = VertexArray(generatedData.vertexData)
    private val drawList = generatedData.drawList


    override fun bindData(shaderProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            shaderProgram.aPositionLocation,
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    override fun draw() {
        drawList.forEach { it.draw() }
    }

}
