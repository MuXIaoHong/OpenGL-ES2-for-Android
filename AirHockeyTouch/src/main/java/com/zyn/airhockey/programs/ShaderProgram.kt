package com.zyn.airhockey.programs

import android.content.Context
import android.opengl.GLES20
import com.zyn.airhockey.util.ShaderHelper
import com.zyn.airhockey.util.TextResourceReader

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
open class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int) {
    companion object {
        //Uniform constants
        const val U_MATRIX = "u_Matrix"
        const val U_TEXTURE_UNIT = "u_TextureUnit"

        //Attribute constants
        const val A_POSITION = "a_Position"
        const val A_COLOR = "a_Color"
        const val U_COLOR = "u_Color"
        const val A_TEXTURE_COORDINATES = "a_TextureCoordinates"
    }

    protected val program: Int = ShaderHelper.buildProgram(
        TextResourceReader.readTextFileFromResource(
            context,
            vertexShaderResourceId
        ), TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
    )

     fun userProgram(){
        GLES20.glUseProgram(program)
    }

}