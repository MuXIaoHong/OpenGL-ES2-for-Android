package com.zyn.airhockey.objects

import android.opengl.GLES20.*
import com.zyn.airhockey.util.Geometry

/**
 * @author zyn
 * @date 2022/4/16
 * 物体构建器
 * @param sizeInVertices 需要的顶点数量
 */
class ObjectBuilder(val sizeInVertices: Int) {
    interface DrawCommand {
        fun draw()
    }

    class GeneratedData(val vertexData: FloatArray, val drawList: List<DrawCommand>)

    companion object {
        const val FLOATS_PER_VERTEX = 3

        /**
         * 计算圆柱体顶部顶点数量
         * 一个圆柱体的顶部是一个用三角形扇构造的圆；它有一个顶点在圆心，围着圆的每个点都有一个顶点
         * 并且围着圆的第一个顶点要重复两次才能使圆闭合
         */
        fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        /**
         * 一个圆柱体的侧面是一个卷起来的长方形，由一个三角形带构造，围着顶部圆的每个点都需要两个顶点，并且前两个顶点要重复两次才能使这个管闭合
         */
        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }

        /**
         * 创建一个冰球
         */
        fun createPuck(puck: Geometry.Cylinder, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)
            val objectBuilder = ObjectBuilder(size)

            val puckTop = Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius)
            objectBuilder.appendCircle(puckTop, numPoints)
            objectBuilder.appendOpenCylinder(puck, numPoints)
            return objectBuilder.build()
        }

        /**
         * 创建木槌
         */
        fun createMallet(center: Geometry.Point, radius: Float, height: Float, numPoints: Int): GeneratedData {
            val size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2
            val objectBuilder = ObjectBuilder(size)
            val baseHeight = height * 0.25f
            val baseCircle = Geometry.Circle(center.translateY(-baseHeight), radius)
            val baseCylinder = Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius,
                baseHeight
            )
            objectBuilder.appendCircle(baseCircle,numPoints)
            objectBuilder.appendOpenCylinder(baseCylinder,numPoints)

            //手柄
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f
            val handleCircle = Geometry.Circle(center.translateY(height * 0.5f), handleRadius)
            val handleCylinder = Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius,
                handleHeight
            )
            objectBuilder.appendCircle(handleCircle,numPoints)
            objectBuilder.appendOpenCylinder(handleCylinder,numPoints)
            return objectBuilder.build()
        }
    }

    private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)
    private val drawList = ArrayList<DrawCommand>()

    /**用于记录数组中下一个顶点的位置*/
    private var offset = 0


    fun appendCircle(circle: Geometry.Circle, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        //三角形扇的Center
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints.toFloat()) * (Math.PI * 2f)
            vertexData[offset++] =
                (circle.center.x + circle.radius * kotlin.math.cos(angleInRadians)).toFloat()
            vertexData[offset++] = circle.center.y
            vertexData[offset++] =
                (circle.center.z + circle.radius * Math.sin(angleInRadians)).toFloat()

        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })

    }

    fun appendOpenCylinder(cylinder: Geometry.Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertex = sizeOfOpenCylinderInVertices(numPoints)
        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        for (i in 0..numPoints) {
            val angleInRadians = (i.toFloat() / numPoints.toFloat()) * (Math.PI * 2f)
            val xPosition = cylinder.center.x + cylinder.radius * Math.cos(angleInRadians)
            val zPosition = cylinder.center.z + cylinder.radius * Math.sin(angleInRadians)
            vertexData[offset++] = xPosition.toFloat()
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition.toFloat()
            vertexData[offset++] = xPosition.toFloat()
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition.toFloat()
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertex)
            }
        })

    }

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

}