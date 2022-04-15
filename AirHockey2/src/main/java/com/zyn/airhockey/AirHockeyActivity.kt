package com.zyn.airhockey

import android.app.ActivityManager
import android.content.Context
import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class AirHockeyActivity : AppCompatActivity() {
    private lateinit var glSurfaceView: GLSurfaceView
    private var rendererSet: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glSurfaceView = GLSurfaceView(this)

        //检查系统是否支持OpenGL ES 2.0
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val deviceConfigurationInfo = activityManager.deviceConfigurationInfo
        val supportsEs2 = deviceConfigurationInfo.reqGlEsVersion >= 0X20000

        if (supportsEs2){
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(AirHockeyRender(this))
            rendererSet = true
        }else{
            Toast.makeText(this, "不支持OpenGL ES 2.0", Toast.LENGTH_SHORT).show()
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