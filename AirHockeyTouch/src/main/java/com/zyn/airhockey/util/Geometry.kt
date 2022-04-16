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
    class Point(val x: Float, val y: Float, val z: Float) {
        fun translateY(distance: Float): Point {
            return Point(x, y + distance, z)
        }

        fun translate(vector: Vector): Point {
            return Point(x + vector.x, y + vector.y, z + vector.z)
        }
    }

    class Circle(val center: Point, val radius: Float) {
        fun scale(scale: Float): Circle {
            return Circle(center, radius * scale)
        }
    }

    /**
     * 圆柱
     */
    class Cylinder(val center: Point, val radius: Float, val height: Float) {

    }

    /**
     * 射线
     */
    class Ray(val point: Point, val vector: Vector) {

    }

    class Vector(val x: Float, val y: Float, val z: Float) {
        /**
         * 利用勾股定理返回向量的长度
         */
        fun length(): Float {
            return Math.sqrt(
                (x * x + y * y + z * z).toDouble()
            ).toFloat()
        }

        /**
         * 计算两个向量的交叉乘积
         */
        fun crossProduct(other:Vector): Vector {
            return Vector(
                (y*other.z) - (z*other.y),
                (z*other.x) - (x*other.z),
                (x*other.y) - (y*other.x)
            )
        }

        /**
         * 计算两个向量之间的点积
         */
        fun doProduct(other: Vector): Float {
            return x*other.x+y*other.y+z*other.z
        }

        fun scale(f:Float): Vector {
            return Vector(
                x*f,
                y*f,
                z*f
            )
        }
    }

    /**
     * 球
     */
    class Sphere(val center: Point, val radius: Float) {

    }

    /**
     * 平面
     * 平面包含一个法向向量和平面上的一个点
     *
     */
    class Plane(val point:Point,val normal:Vector){

    }

    companion object {
        fun vectorBetween(from: Point, to: Point): Vector {
            return Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z
            )
        }

        /**
         * 用向量计算距离
         */
        fun distanceBetween(point: Point, ray: Ray): Float {
            val p1ToPoint = vectorBetween(ray.point, point)
            val p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point)

            val areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length()
            val lengthOfBase = ray.vector.length()

            return areaOfTriangleTimesTwo / lengthOfBase

        }

        fun intersects(sphere: Sphere, ray: Ray): Boolean {
            return distanceBetween(sphere.center, ray) < sphere.radius
        }

        /**
         * 计算交点
         */
        fun intersectionPoint(ray: Ray, plane: Plane): Point {
            val rayToPlaneVector = vectorBetween(ray.point, plane.point)
            val scaleFactor =
                rayToPlaneVector.doProduct(plane.normal) / ray.vector.doProduct(plane.normal)
            return ray.point.translate(ray.vector.scale(scaleFactor))
        }
    }
}