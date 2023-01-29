package com.airhockey.android.util

class Geometry {

    data class Point(
        val x: Float,
        val y: Float,
        val z: Float,
    ) {
        fun translateY(distance: Float) = Point(x, y + distance, z)
    }

    data class Circle(
        val center: Point,
        val radius: Float,
    )

    data class Cylinder(
        val center: Point,
        val radius: Float,
        val height: Float,
    )
}