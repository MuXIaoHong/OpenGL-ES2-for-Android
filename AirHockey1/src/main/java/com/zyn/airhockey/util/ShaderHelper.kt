package com.zyn.airhockey.util

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.util.Log

/**
 * @author zyn
 * @date 2022/4/13
 *
 */
private const val TAG = "ShaderHelper"

class ShaderHelper {
    companion object {
        fun compileVertexShader(shaderCode: String): Int {
            return compileShader(GL_VERTEX_SHADER, shaderCode)
        }

        fun compileFragmentShader(shaderCode: String): Int {
            return compileShader(GL_FRAGMENT_SHADER, shaderCode)
        }



        /**
         * 加载和编译着色器
         */
        private fun compileShader(type: Int, shaderCode: String): Int {
            val shaderObjectId = glCreateShader(type)
            if (shaderObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new shader.")
                }
                return 0
            }
            //上传着色器源代码
            glShaderSource(shaderObjectId, shaderCode)
            //编译着色器
            glCompileShader(shaderObjectId)
            //取出编译状态,offset表示查询结果放在compileStatus的第0个位置
            val compileStatus = intArrayOf(0)
            glGetShaderiv(shaderObjectId, GL_COMPILE_STATUS, compileStatus, 0)
            //取出着色器信息日志
            if (LoggerConfig.ON) {
                Log.v(
                    TAG,
                    "Result of compiling source:\n$shaderCode\n${glGetShaderInfoLog(shaderObjectId)}"
                )
            }
            if (compileStatus[0] == 0) {
                //编译失败
                glDeleteShader(shaderObjectId)
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Compilation of shader failed.")
                }
                return 0
            }
            return shaderObjectId

        }


        /**
         * 把着色器一起链接进一个Program
         */
        fun linkProgram(vertexShaderId: Int, fragmentShaderId: Int): Int {
            val programObjectId = glCreateProgram()
            if (programObjectId == 0) {
                if (LoggerConfig.ON) {
                    Log.w(TAG, "Could not create new program.")
                }
                return 0
            }
            glAttachShader(programObjectId,vertexShaderId)
            glAttachShader(programObjectId,fragmentShaderId)
            glLinkProgram(programObjectId)
            val linkStatus = intArrayOf(0)
            glGetProgramiv(programObjectId, GL_LINK_STATUS,linkStatus,0)
            if (LoggerConfig.ON){
                Log.v(TAG,"Result of linking program:\n${glGetProgramInfoLog(programObjectId)}")
            }
            if (linkStatus[0] == 0){
                glDeleteProgram(programObjectId)
                if (LoggerConfig.ON){
                    Log.w(TAG,"Linking of program failed.")
                }
                return 0
            }
            return programObjectId
        }

        /**
         * 使用程序之前，要先验证一下
         */
        fun validateProgram(programObjectId:Int): Boolean {
            glValidateProgram(programObjectId)
            val validateStatus = intArrayOf(0)
            glGetProgramiv(programObjectId, GL_VALIDATE_STATUS,validateStatus,0)
            if (LoggerConfig.ON){
                Log.v(TAG,"Result of validating program:${validateStatus[0]}\nLog:${glGetProgramInfoLog(programObjectId)}")
            }
            return validateStatus[0] != 0
        }




    }
}