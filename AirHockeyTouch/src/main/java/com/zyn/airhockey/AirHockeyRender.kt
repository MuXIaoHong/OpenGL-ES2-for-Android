package com.zyn.airhockey

import android.content.Context
import android.graphics.Point
import android.media.Image
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.zyn.airhockey.objects.Mallet
import com.zyn.airhockey.objects.Puck
import com.zyn.airhockey.objects.Table
import com.zyn.airhockey.programs.ColorShaderProgram
import com.zyn.airhockey.programs.TextureShaderProgram
import com.zyn.airhockey.util.Geometry
import com.zyn.airhockey.util.LoggerConfig
import com.zyn.airhockey.util.MatrixHelper
import com.zyn.airhockey.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author zyn
 * @date 2022/4/12
 *
 */
private const val TAG = "AirHockeyRender"
class AirHockeyRender(val context: Context) : GLSurfaceView.Renderer {


    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)

    //利用模型矩阵移动物体
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck

    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram

    private var texture: Int = 0

    private var malletPressed = false
    private lateinit var blueMalletPosition: Geometry.Point


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)
        table = Table()
        mallet = Mallet(0.08f,0.15f,32)
        puck = Puck(0.06f,0.02f,32)
        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context,R.drawable.air_hockey_surface)

        blueMalletPosition = Geometry.Point(0f, mallet.height / 2f, 0.4f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        MatrixHelper.perspectiveM(
            projectionMatrix,
            45f,
            width.toFloat() / height.toFloat(),
            1f,
            10f
        )
        Matrix.setLookAtM(viewMatrix,0,0f,1.2f,2.2f,0f,0f,0f,0f,1f,0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        Matrix.multiplyMM(viewProjectionMatrix,0,projectionMatrix,0,viewMatrix,0)
        Matrix.invertM(invertedViewProjectionMatrix,0,viewProjectionMatrix,0)

        positionTableInScene()
        textureProgram.userProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix,texture)
        table.bindData(textureProgram)
        table.draw()

        positionObjectInScene(0f,mallet.height / 2f,-0.4f)
        colorProgram.userProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix,1f,0f,0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        positionObjectInScene(blueMalletPosition.x,blueMalletPosition.y,blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix,0f,0f,1f)
        mallet.draw()

        positionObjectInScene(0f,puck.height /2f,0f)
        colorProgram.setUniforms(modelViewProjectionMatrix,0.8f,0.8f,1f)
        puck.bindData(colorProgram)
        puck.draw()
    }

    private fun positionTableInScene(){
        Matrix.setIdentityM(modelMatrix,0)
        Matrix.rotateM(modelMatrix,0,-90f,1f,0f,0f)
        Matrix.multiplyMM(modelViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0)
    }

    private fun positionObjectInScene(x:Float,y:Float,z:Float){
        Matrix.setIdentityM(modelMatrix,0)
        Matrix.translateM(modelMatrix,0,x,y,z)
        Matrix.multiplyMM(modelViewProjectionMatrix,0,viewProjectionMatrix,0,modelMatrix,0)
    }

    private fun divideByW(vector:FloatArray){
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    /**
     * 把被触碰的点转化为一个三维射线
     */
    private fun convertNormalized2DPointToRay(normalizedX: Float,normalizedY: Float): Geometry.Ray {
        val nearPointNdc = floatArrayOf(normalizedX,normalizedY,-1f,1f)
        val farPointNdc = floatArrayOf(normalizedX,normalizedY,1f,1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        Matrix.multiplyMV(nearPointWorld,0,invertedViewProjectionMatrix,0,nearPointNdc,0)
        Matrix.multiplyMV(farPointWorld,0,invertedViewProjectionMatrix,0,farPointNdc,0)

        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        val nearPointRay = Geometry.Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = Geometry.Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])
        return Geometry.Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay))
    }


    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        val ray = convertNormalized2DPointToRay(normalizedX,normalizedY)
        val malletBoundingSphere = Geometry.Sphere(
            Geometry.Point(
                blueMalletPosition.x,
                blueMalletPosition.y,
                blueMalletPosition.z
            ), mallet.height / 2f
        )
        malletPressed = Geometry.intersects(malletBoundingSphere,ray).also {
            if (LoggerConfig.ON){
                Log.d(TAG,"malletPressed:$malletPressed")
            }
        }
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        if (malletPressed){
            val ray =
                convertNormalized2DPointToRay(normalizedX, normalizedY)
            val plane = Geometry.Plane(Geometry.Point(0f, 0f, 0f), Geometry.Vector(0f, 1f, 0f))
            val touchedPoint = Geometry.intersectionPoint(ray, plane)
            blueMalletPosition = Geometry.Point(touchedPoint.x, mallet.height / 2f, touchedPoint.z)
        }
    }

}