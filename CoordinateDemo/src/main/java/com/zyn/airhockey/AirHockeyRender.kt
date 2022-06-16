package com.zyn.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
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
        const val POSITION_COMPONENT_COUNT = 3
        const val BYTES_PER_FLOAT = 4
        //为uniform创建一个常量保存其名字
        const val U_COLOR = "u_Color"
        //为attribute创建一个常量保存其名字
        const val A_POSITION = "a_Position"
    }
    //为attribute创建一个变量用来容纳它在OpenGL程序对象中的位置
    private var uColorLocation:Int = 0
    //为uniform创建一个变量用来容纳它在OpenGL程序对象中的位置
    private var aPositionLocation:Int = 0
    private val vertexData:FloatBuffer
    private var program:Int = 0

    init {
        val tableVerticesWithTriangles: FloatArray = floatArrayOf(
            //x轴
            -1f, 0f,0f,
            1f, 0f,0f,

            //y轴
            0f,-1f,0f,
            0f,1f,0f,

            1f,0f,0f,
            0f,1f,0f

        )
        //使数据可以被OpenGL读取，（java数据转换成native可以读取的数据）
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size*BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    //投影
    private val projectionMatrix = FloatArray(16)


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置清空屏幕用的颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_vertex_shader)
        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context,R.raw.simple_fragment_shader)
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        program = ShaderHelper.linkProgram(vertexShader,fragmentShader)
        if (LoggerConfig.ON){
            ShaderHelper.validateProgram(program)
        }
        glUseProgram(program)
        //调用glGetUniformLocation获取uniform的位置，并存入uColorLocation，要更新这个uniform值的时候，使用并存入uColorLocation
        uColorLocation = glGetUniformLocation(program, U_COLOR)
        //调用glGetAttribLocation获取attribute的位置，有了这个位置，就能告诉OpenGL到哪里去找到这个属性对应的数据了
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        //告诉OpenGL去哪里找到属性a_Position对应的数据
        vertexData.position(0)
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,false,0,vertexData)
        //使能这个属性
        glEnableVertexAttribArray(aPositionLocation)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 2f, 10f)
        Matrix.setIdentityM(projectionMatrix,0)
        Matrix.rotateM(projectionMatrix,0,30f,0f,0f,1f)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有的颜色
        glClear(GL_COLOR_BUFFER_BIT)
        val mvpMatrixHandle = glGetUniformLocation(program, "u_MVP")
        glUniformMatrix4fv(mvpMatrixHandle, 1, false, projectionMatrix, 0)

        //绘制分割线
        //更新着色器代码中u_Color的点值
        glUniform4f(uColorLocation,1.0f,0.0f,0.0f,1.0f)
        glDrawArrays(GL_LINES,0,2)

        glDrawArrays(GL_LINES,2,2)

        glDrawArrays(GL_POINTS,4,1)

        glDrawArrays(GL_POINTS,5,1)

    }
}