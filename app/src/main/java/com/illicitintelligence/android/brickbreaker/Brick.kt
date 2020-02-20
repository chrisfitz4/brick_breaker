package com.illicitintelligence.android.brickbreaker

import android.graphics.PointF
import android.graphics.Rect

enum class Property{
    NORMAL,
    UNBREAKABLE,
    SPECIAL
}
enum class HitType{
    HORIZONTAL,
    VERTICAL,
    DIAGONAL_TOP_LEFT,
    DIAGONAL_TOP_RIGHT,
    DIAGONAL_BOTTOM_LEFT,
    DIAGONAL_BOTTOM_RIGHT,
    ERROR
}

data class Brick(val rect: Rect, val color: Int, val property: Property){

    fun hitRight(point: PointF): Boolean{
        return point.x>rect.right
    }
    fun hitLeft(point: PointF): Boolean{
        return point.x<rect.left
    }
    fun hitTop(point: PointF): Boolean{
        return point.y<rect.top
    }
    fun hitBottom(point: PointF): Boolean{
        return point.y>rect.bottom
    }

    fun hitType(point: PointF): HitType{
        val hitTop = hitTop(point)
        val hitBottom = hitBottom(point)
        val hitRight = hitRight(point)
        val hitLeft = hitLeft(point)
        if(hitTop){
            return when {
                hitLeft -> HitType.DIAGONAL_TOP_LEFT
                hitRight -> HitType.DIAGONAL_TOP_RIGHT
                else -> HitType.VERTICAL
            }
        }else if(hitBottom){
            return when {
                hitLeft -> HitType.DIAGONAL_BOTTOM_LEFT
                hitRight -> HitType.DIAGONAL_BOTTOM_RIGHT
                else -> HitType.VERTICAL
            }
        }else if(hitRight||hitLeft){
            return HitType.HORIZONTAL
        }else {
            return HitType.ERROR
        }
    }
}