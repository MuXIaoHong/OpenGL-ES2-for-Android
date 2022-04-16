package com.zyn.airhockey

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast

class AirHockeyActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private var rendererSet: Boolean = false
    val airHockeyRenderer = AirHockeyRender(this)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)

        //检查系统是否支持OpenGL ES 2.0
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val deviceConfigurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0X20000

        if (supportsEs2){
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(airHockeyRenderer)
            rendererSet = true
        }else{
            Toast.makeText(this, "不支持OpenGL ES 2.0", Toast.LENGTH_SHORT).show()
        }

        glSurfaceView.setOnTouchListener { v, event ->
            if (event !=null){
                //把触控事件坐标转换回归一化设备坐标
                val normalizedX = (event.x /v.width.toFloat())*2 -1
                val normalizedY = -((event.y /v.height.toFloat())*2 -1)
                when(event.action){
                    MotionEvent.ACTION_DOWN->{
                        glSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchPress(normalizedX,normalizedY)
                        }
                    }
                    MotionEvent.ACTION_MOVE->{
                        glSurfaceView.queueEvent {
                            airHockeyRenderer.handleTouchDrag(normalizedX,normalizedY)
                        }
                    }
                }
                return@setOnTouchListener true
            }else{
                return@setOnTouchListener  false
            }
        }
        setContentView(glSurfaceView)

    }

    override fun onPause() {
        super.onPause()
        if (rendererSet){
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (rendererSet){
            glSurfaceView.onResume()
        }
    }
}