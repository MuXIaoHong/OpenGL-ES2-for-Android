package com.zyn.airhockey.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log

/**
 * @author zyn
 * @date 2022/4/15
 * 纹理工具类
 */
private const val TAG = "TextureHelper"
object TextureHelper {
    fun loadTexture(context: Context,resourceId:Int): Int {
        val textureObjectIds = IntArray(1)
        glGenTextures(1,textureObjectIds,0)
        if (textureObjectIds[0] == 0){
            if (LoggerConfig.ON){
                Log.w(TAG,"Could not generate a new OpenGL texture object.")
            }
            return 0
        }
        val options = BitmapFactory.Options().apply {
            inScaled = false
        }
        val bitmap = BitmapFactory.decodeResource(context.resources,resourceId,options)
        if (bitmap == null){
            if (LoggerConfig.ON){
                Log.w(TAG,"Resource ID $resourceId could not be decoded")
            }
            glDeleteTextures(1,textureObjectIds,0)
            return 0
        }
        glBindTexture(GL_TEXTURE_2D,textureObjectIds[0])
        //设置缩小的时候的纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        //设置放大的时候的纹理过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        GLUtils.texImage2D(GL_TEXTURE_2D,0,bitmap,0)
        bitmap.recycle()

        //生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D)

        //既然完成纹理的加载，就可以解除与这个纹理的绑定，这样就不会用其他纹理方法调用意外地改变这个纹理
        glBindTexture(GL_TEXTURE_2D,0)
        return  textureObjectIds[0]
    }
}