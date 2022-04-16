package com.zyn.airhockey.programs

import android.content.Context
import android.opengl.GLES20.*
import com.zyn.airhockey.R

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class ColorShaderProgram(context: Context): ShaderProgram(context, R.raw.texture_vertex_shader,R.raw.texture_fragment_shader) {
    //Uniform locations
    private val uMatrixLocation:Int = glGetUniformLocation(program, U_MATRIX)

    //Attribute locations
    private val aPositionLocation:Int = glGetAttribLocation(program, A_POSITION)
    private val aColorLocation:Int = glGetAttribLocation(program, A_COLOR)

    /**
     * 传递矩阵和纹理给它们的uniform
     */
    fun setUniforms(matrix:FloatArray){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0)

    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getColorAttributeLocation(): Int {
        return aColorLocation
    }

}