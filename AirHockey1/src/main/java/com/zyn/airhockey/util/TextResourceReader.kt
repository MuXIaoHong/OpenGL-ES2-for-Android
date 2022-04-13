package com.zyn.airhockey.util

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder

/**
 * @author zyn
 * @date 2022/4/12
 *
 */
object TextResourceReader {
    fun readTextFileFromResource(context: Context,resourceId:Int): String {
        val body = StringBuilder()
        val inputStream = context.resources.openRawResource(resourceId)
        val inputStreamReader = InputStreamReader(inputStream)
        val bufferedReader = BufferedReader(inputStreamReader)

        var nextLine:String?=bufferedReader.readLine()
        while (nextLine != null){
            body.append(nextLine)
            body.append("\n")
            nextLine = bufferedReader.readLine()
        }

        return body.toString()
    }
}