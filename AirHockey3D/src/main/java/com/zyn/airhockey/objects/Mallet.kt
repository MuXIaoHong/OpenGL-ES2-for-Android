package com.zyn.airhockey.objects

import android.opengl.GLES20
import android.opengl.GLES20.*
import com.zyn.airhockey.BYTES_PER_FLOAT
import com.zyn.airhockey.data.VertexArray
import com.zyn.airhockey.programs.ColorShaderProgram

/**
 * @author zyn
 * @date 2022/4/16
 * 木槌
 */
class Mallet {
    companion object {
        //位置分量计数
        const val POSITION_COMPONENT_COUNT = 2
        //颜色分量计数
        const val COLOR_COMPONENT_COUNT = 3
        //跨距
        const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
        //顶点数据
        val VERTEX_DATA = floatArrayOf(
            //Order of coordinates:X,Y,R,G,B
            0f,-0.4f,0f,0f,1f,
            0f,0.4f,1f,0f,0f
        )
    }
    private val vertexArray = VertexArray(VERTEX_DATA)

    /**
     * 把顶点数组绑定到一个着色器程序上
     */
    fun bindData(colorProgram: ColorShaderProgram){
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.getColorAttributeLocation(),
            COLOR_COMPONENT_COUNT,
            STRIDE
        )

    }
    fun draw(){
        glDrawArrays(GL_POINTS,0,2)
    }
}