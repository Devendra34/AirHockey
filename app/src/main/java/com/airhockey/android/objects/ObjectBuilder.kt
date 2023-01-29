package com.airhockey.android.objects

import android.opengl.GLES20.*
import com.airhockey.android.objects.ObjectBuilder.DrawCommand
import com.airhockey.android.util.Geometry.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ObjectBuilder(sizeInVertices: Int) {

    private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private var offset = 0
    private val drawList = mutableListOf<DrawCommand>()

    companion object {
        private const val FLOATS_PER_VERTEX = 3


        /**
         * Number of vertices required to draw a circle using [GL_TRIANGLE_FAN]
         * @param numOfPoints: Number of points in the circumference
         * @return Total points for drawing a circle using [GL_TRIANGLE_FAN].
         * This will include:
         * 1 center point + (number of circumference points) + first point to complete the circle
         */
        @JvmStatic
        private fun sizeOfCircleInVertices(numOfPoints: Int) = (1 + (numOfPoints + 1))

        /**
         * A cylinder side is a rolled-up rectangle built out of a triangle strip,
         * with two vertices for each point around the circle,
         * and with the first two vertices repeated twice so that we can close off the tube.
         */
        @JvmStatic
        private fun sizeOfOpenCylinderInVertices(numPoints: Int) = ((numPoints + 1) * 2)

        @JvmStatic
        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)

            val objectBuilder = ObjectBuilder(size)
            val puckTop = Circle(puck.center.translateY(puck.height / 2f), puck.radius)
            objectBuilder.appendCircle(puckTop, numPoints)
            objectBuilder.appendOpenCylinder(puck, numPoints)

            return objectBuilder.build()
        }

        @JvmStatic
        fun createMallet(
            center: Point,
            radius: Float,
            height: Float,
            numPoints: Int
        ): GeneratedData {
            // 2X vertices needed to draw a mallet of 2 cylinders on top of other
            val circleVertices = 2 * sizeOfCircleInVertices(numPoints)
            val cylinderVertices = 2 * sizeOfOpenCylinderInVertices(numPoints)
            val size = circleVertices + cylinderVertices

            val objectBuilder = ObjectBuilder(size)

            // generate mallet base
            val baseHeight = height * 0.25f
            val baseTopCircle = Circle(center.translateY(-baseHeight), radius)
            val baseCylinderCenter = baseTopCircle.center.translateY(-baseHeight / 2f)
            val baseCylinder = Cylinder(baseCylinderCenter, radius, baseHeight)
            objectBuilder.appendCircle(baseTopCircle, numPoints)
            objectBuilder.appendOpenCylinder(baseCylinder, numPoints)

            // generate mallet handle
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f
            val handleTopCircle = Circle(center.translateY(height / 2f), handleRadius)
            val handleCylinderCenter = handleTopCircle.center.translateY(-handleHeight / 2f)
            val handleCylinder = Cylinder(handleCylinderCenter, handleRadius, handleHeight)
            objectBuilder.appendCircle(handleTopCircle, numPoints)
            objectBuilder.appendOpenCylinder(handleCylinder, numPoints)

            return objectBuilder.build()
        }

    }

    private fun appendCircle(circle: Circle, numPoints: Int) {
        val startVertex: Int = offset / FLOATS_PER_VERTEX
        val numberOfVertex: Int = sizeOfCircleInVertices(numPoints)

        // Add center point of fan in vertex data
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        // Fan around the center point.
        // Use <= to ensure we complete the circle by ending with start point again.
        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints) * PI.toFloat() * 2f

            vertexData[offset++] = circle.center.x + circle.radius * cos(angleInRadians)
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z + circle.radius * sin(angleInRadians)
        }
        drawList.add(DrawCommand {
            glDrawArrays(GL_TRIANGLE_FAN, startVertex, numberOfVertex)
        })
    }

    private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {

        val startVertex: Int = offset / FLOATS_PER_VERTEX
        val numberOfVertex: Int = sizeOfOpenCylinderInVertices(numPoints)

        val yStart = cylinder.center.y + (cylinder.height / 2f)
        val yEnd = cylinder.center.y - (cylinder.height / 2f)

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints) * PI.toFloat() * 2f

            val xPosition = cylinder.center.x + cylinder.radius * cos(angleInRadians)
            val zPosition = cylinder.center.z + cylinder.radius * sin(angleInRadians)

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition

            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(DrawCommand {
            glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numberOfVertex)
        })
    }

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

    class GeneratedData(
        val vertexData: FloatArray,
        val drawList: List<DrawCommand>
    )

    fun interface DrawCommand {
        fun draw()
    }
}