package com.zyn.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glUseProgram
import android.opengl.GLSurfaceView
import com.zyn.airhockey.util.LoggerConfig
import com.zyn.airhockey.util.ShaderHelper
import com.zyn.airhockey.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author zyn
 * @date 2022/4/12
 *
 */
class AirHockeyRender (val context: Context) : GLSurfaceView.Renderer {
    companion object {
        const val POSITION_COMPONENT_COUNT = 2
        const val BYTES_PER_FLOAT = 4
    }

    private val vertexData:FloatBuffer
    private var program:Int = 0

    init {
        val tableVerticesWithTriangles: FloatArray = floatArrayOf(
            //三角形1
            0f, 0f,
            9f, 14f,
            0f, 14f,
            //三角形2
            0f, 0f,
            9f, 0f,
            9f, 14f,
            //线1
            0f, 7f,
            9f, 7f,
            //木槌
            4.5f, 2f,
            4.5f, 12f
        )
        //使数据可以被OpenGL读取，（java数据转换成native可以读取的数据）
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size*BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置清空屏幕用的颜色
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_vertex_shader)
        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_fragment_shader)
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        program = ShaderHelper.linkProgram(vertexShader,fragmentShader)
        if (LoggerConfig.ON){
            ShaderHelper.validateProgram(program)
        }
        glUseProgram(program)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有的颜色
        GLES20.glClear(GL_COLOR_BUFFER_BIT)
    }
}