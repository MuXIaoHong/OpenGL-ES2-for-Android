package com.zyn.airhockey.objects

import android.opengl.GLES20
import android.opengl.GLES20.*
import com.zyn.airhockey.BYTES_PER_FLOAT
import com.zyn.airhockey.data.VertexArray
import com.zyn.airhockey.programs.TextureShaderProgram

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class Table {
    companion object {
        //位置分量计数
        const val POSITION_COMPONENT_COUNT = 2
        //纹理坐标分量计数
        const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        //跨距
        const val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT
        //顶点数据
        val VERTEX_DATA = floatArrayOf(
            //Order of coordinates:X,Y,S,T
            0f,0f,0.5f,0.5f,
            -0.5f,-0.8f,0f,0.9f,
            0.5f,-0.8f,1f,0.9f,
            0.5f,0.8f,1f,0.1f,
            -0.5f,0.8f,0f,0.1f,
            -0.5f,-0.8f,0f,0.9f
        )
    }
    private val vertexArray = VertexArray(VERTEX_DATA)

    /**
     * 把顶点数组绑定到一个着色器程序上
     */
    fun bindData(textureProgram: TextureShaderProgram){
        vertexArray.setVertexAttribPointer(
            0,
            textureProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureProgram.getTextureCoordinatesAttributeLocation(),
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )

    }
    fun draw(){
        glDrawArrays(GL_TRIANGLE_FAN,0,6)
    }
}