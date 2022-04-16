package com.zyn.airhockey.objects

import android.opengl.GLES20
import com.zyn.airhockey.BYTES_PER_FLOAT
import com.zyn.airhockey.data.VertexArray
import com.zyn.airhockey.programs.ColorShaderProgram
import com.zyn.airhockey.util.Geometry

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class Puck(val radius: Float, val height: Float, val numPointsAroundPuck: Int) {
    companion object {
        //位置分量计数
        const val POSITION_COMPONENT_COUNT = 3

    }

    private var vertexArray: VertexArray
    private var drawList: List<ObjectBuilder.DrawCommand>

    init {
        val puckData = ObjectBuilder.createPuck(
            Geometry.Cylinder(Geometry.Point(0f, 0f, 0f), radius, height),
            numPointsAroundPuck
        )
        vertexArray = VertexArray(puckData.vertexData)
        drawList = puckData.drawList
    }

    /**
     * 把顶点数组绑定到一个着色器程序上
     */
    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    fun draw() {
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
    }

}