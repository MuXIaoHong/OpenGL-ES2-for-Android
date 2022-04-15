package com.zyn.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
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
class AirHockeyRender(val context: Context) : GLSurfaceView.Renderer {
    companion object {
        const val POSITION_COMPONENT_COUNT = 2
        const val BYTES_PER_FLOAT = 4

        //为uniform创建一个常量保存其名字
        const val A_COLOR = "a_Color"

        //为attribute创建一个常量保存其名字
        const val A_POSITION = "a_Position"
        const val COLOR_COMPONENT_COUNT = 3
        //跨距
        const val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT
    }


    //为uniform创建一个变量用来容纳它在OpenGL程序对象中的位置
    private var aPositionLocation: Int = 0
    private var aColorLocation: Int = 0
    private val vertexData: FloatBuffer
    private var program: Int = 0

    init {
//        val tableVerticesWithTriangles: FloatArray = floatArrayOf(
//            //三角形1
//            0f, 0f,
//            9f, 14f,
//            0f, 14f,
//            //三角形2
//            0f, 0f,
//            9f, 0f,
//            9f, 14f,
//            //线1
//            0f, 7f,
//            9f, 7f,
//            //木槌
//            4.5f, 2f,
//            4.5f, 12f
//        )

//        val tableVerticesWithTriangles: FloatArray = floatArrayOf(
//            //三角形1
//            -0.5f, -0.5f,
//            0.5f, 0.5f,
//            -0.5f, 0.5f,
//            //三角形2
//            -0.5f, -0.5f,
//            0.5f, -0.5f,
//            0.5f, 0.5f,
//            //线1
//            -0.5f, 0f,
//            0.5f, 0f,
//            //木槌
//            0f, -0.25f,
//            0f, 0.25f
//        )

        val tableVerticesWithTriangles: FloatArray = floatArrayOf(
            //三角形扇
            0f, 0f, 1f, 1f, 1f,
            -.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            //三角形2
            0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f, 0.5f, 0.7f, 0.7f, 0.7f,
            -0.5f, -0.5f, 0.7f, 0.7f, 0.7f,
            //线1
            -0.5f, 0f, 1f, 0f, 0f,
            0.5f, 0f, 1f, 0f, 0f,
            //木槌
            0f, -0.25f, 0f, 0f, 1f,
            0f, 0.25f, 1f, 0f, 0f
        )
        //使数据可以被OpenGL读取，（java数据转换成native可以读取的数据）
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //设置清空屏幕用的颜色
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        val vertexShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource =
            TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader)
        val vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource)
        program = ShaderHelper.linkProgram(vertexShader, fragmentShader)
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program)
        }
        glUseProgram(program)
        aColorLocation = glGetAttribLocation(program, A_COLOR)
        //调用glGetAttribLocation获取attribute的位置，有了这个位置，就能告诉OpenGL到哪里去找到这个属性对应的数据了
        aPositionLocation = glGetAttribLocation(program, A_POSITION)

        //告诉OpenGL去哪里找到属性a_Position对应的数据
        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        //使能这个属性
        glEnableVertexAttribArray(aPositionLocation)

        //告诉OpenGL去哪里找到属性a_Position对应的数据
        vertexData.position(POSITION_COMPONENT_COUNT)
        glVertexAttribPointer(
            aColorLocation,
            POSITION_COMPONENT_COUNT,
            GL_FLOAT,
            false,
            STRIDE,
            vertexData
        )
        //使能这个属性
        glEnableVertexAttribArray(aColorLocation)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //擦除屏幕上所有的颜色
        glClear(GL_COLOR_BUFFER_BIT)

        //绘制桌子
        //桌子由两个三角形构成，每个三角形3个顶点
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)

        //绘制分割线
        //从数组第7个顶点开始，一个线有两个顶点
        glDrawArrays(GL_LINES, 6, 2)

        //绘制木槌
        //从数组第7个顶点开始，一个线有两个顶点
        glDrawArrays(GL_POINTS, 8, 1)
        //从数组第7个顶点开始，一个线有两个顶点
        glDrawArrays(GL_POINTS, 9, 1)


    }
}