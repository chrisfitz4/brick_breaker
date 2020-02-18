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
    DIAGONAL,
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
    fun hitTopOrBottom(point: PointF): Boolean{
        return hitTop(point)||hitBottom(point)
    }
    fun hitRightOrLeft(point: PointF): Boolean{
        return hitRight(point)||hitLeft(point)
    }
    fun hitType(point: PointF): HitType{
        val hitTopOrBottom = hitTopOrBottom(point)
        val hitRightOrLeft = hitRightOrLeft(point)
        val hitDiagonal = hitTopOrBottom&&hitRightOrLeft
        if(hitDiagonal){
            return HitType.DIAGONAL
        }
        if(hitRightOrLeft){
            return HitType.HORIZONTAL
        }
        if(hitTopOrBottom){
            return HitType.VERTICAL
        }
        return HitType.ERROR
    }
}