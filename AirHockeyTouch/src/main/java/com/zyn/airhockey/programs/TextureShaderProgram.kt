package com.zyn.airhockey.programs

import android.content.Context
import android.opengl.GLES20.*
import com.zyn.airhockey.R

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class TextureShaderProgram(context: Context): ShaderProgram(context, R.raw.texture_vertex_shader,R.raw.texture_fragment_shader) {
    //Uniform locations
    private val uMatrixLocation:Int = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUniLocation:Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    //Attribute locations
    private val aPositionLocation:Int = glGetAttribLocation(program, A_POSITION)
    private val aTextureCoordinatesLocation:Int = glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    /**
     * 传递矩阵和纹理给它们的uniform
     */
    fun setUniforms(matrix:FloatArray,textureId:Int){
        glUniformMatrix4fv(uMatrixLocation,1,false,matrix,0)

        //把活动的纹理单元设置为纹理单元0
        glActiveTexture(GL_TEXTURE0)
        //把纹理绑定到这个单元
        glBindTexture(GL_TEXTURE_2D,textureId)
        //把被选定的纹理单元传递给片段着色器中的u_TextureUnit
        glUniform1i(uTextureUniLocation,0)
    }

    fun getPositionAttributeLocation(): Int {
        return aPositionLocation
    }

    fun getTextureCoordinatesAttributeLocation(): Int {
        return aTextureCoordinatesLocation
    }

}