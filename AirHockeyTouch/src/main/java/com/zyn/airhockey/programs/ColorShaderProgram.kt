package com.zyn.airhockey.programs

import android.content.Context
import android.opengl.GLES20.*
import com.zyn.airhockey.R

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class ColorShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {
    //Uniform locations
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    //Attribute locations
    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val uColorLocation: Int = glGetUniformLocation(program, U_COLOR)

    /**
     * 传递矩阵和纹理给它们的uniform
     */
    fun setUniforms(matrix: FloatArray, r: Float, g: Float, b: Float) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
        glUniform4f(uColorLocation, r, g, b, 1f)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }


}