package com.zyn.airhockey.util

/**
 * @author zyn
 * @date 2022/4/16
 *
 */
class Geometry {
    /**
     * 表示3D场景中的一个点
     */
    class Point(val x:Float,val y:Float,val z:Float){
        fun translateY(distance:Float): Point {
            return Point(x,y+distance,z)
        }
    }

    class Circle(val center:Point,val radius:Float){
        fun scale(scale:Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    /**
     * 圆柱
     */
    class Cylinder(val center:Point,val radius: Float,val height:Float){

    }
}