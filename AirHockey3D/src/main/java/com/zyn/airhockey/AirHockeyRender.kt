package com.zyn.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import com.zyn.airhockey.objects.Mallet
import com.zyn.airhockey.objects.Table
import com.zyn.airhockey.programs.ColorShaderProgram
import com.zyn.airhockey.programs.TextureShaderProgram
import com.zyn.airhockey.util.*
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

    private val projectionMatrix = FloatArray(16)

    //利用模型矩阵移动物体
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture: Int = 0


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        table = Table()
        mallet = Mallet()

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
//        MatrixHelper.perspectiveM(
//            projectionMatrix,
//            45f,
//            width.toFloat() / height.toFloat(),
//            1f,
//            10f
//        )

        Matrix.setIdentityM(projectionMatrix, 0)
        val ratio = width.toFloat() / height
        //视锥体的z值从-1的位置开始，到-10位置结束，所以z往屏幕内是负的，所以OpenGL是右手？
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 10f)

        glViewport(0, 0, width, height)

        //设置成单位矩阵
        Matrix.setIdentityM(modelMatrix, 0)
        //沿着z负方向移动2个单位，因为桌子默认Z都是0，在视锥体内【-1，-10】是看不到的
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f)
        val temp = FloatArray(16)
//        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2.5f)
        Matrix.rotateM(modelMatrix, 0, -45f, 1f, 0f, 0f)
//        Matrix.rotateM(modelMatrix, 0, 45f, 0f, 0f, 1f)
        Matrix.multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0)
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.size)

    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        textureProgram.userProgram()
        textureProgram.setUniforms(projectionMatrix,texture)
        table.bindData(textureProgram)
        table.draw()

        colorProgram.userProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }

}