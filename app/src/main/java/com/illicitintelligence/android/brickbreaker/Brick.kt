package com.illicitintelligence.android.brickbreaker

import android.graphics.PointF
import android.graphics.Rect

enum class Property{
    NORMAL,
    UNBREAKABLE,
    SPECIAL
}
enum class HitType{
    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    DIAGONAL_TOP_LEFT,
    DIAGONAL_TOP_RIGHT,
    DIAGONAL_BOTTOM_LEFT,
    DIAGONAL_BOTTOM_RIGHT,
    ERROR
}

class Brick(val rect: Rect, val color: Int, val property: Property, val column: Int, val row: Int){

    private fun hitRight(point: PointF): Boolean{
        return point.x>rect.right
    }
    private fun hitLeft(point: PointF): Boolean{
        return point.x<rect.left
    }
    private fun hitTop(point: PointF): Boolean{
        return point.y<rect.top
    }
    private fun hitBottom(point: PointF): Boolean{
        return point.y>rect.bottom
    }

    fun hitType(
        point: PointF,
        brickAboveExists: Boolean,
        brickBelowExists: Boolean,
        brickToLeftExists: Boolean,
        brickToRightExists: Boolean
    ): HitType{
        val hitTop = hitTop(point)
        val hitBottom = hitBottom(point)
        val hitRight = hitRight(point)
        val hitLeft = hitLeft(point)
        return if(hitTop){
            when {
                hitLeft -> {
                    when {
                        brickAboveExists -> HitType.LEFT
                        brickToLeftExists -> HitType.TOP
                        else -> HitType.DIAGONAL_TOP_LEFT
                    }
                }
                hitRight -> {
                    when{
                        brickAboveExists-> HitType.RIGHT
                        brickToRightExists-> HitType.TOP
                        else-> HitType.DIAGONAL_TOP_RIGHT
                    }
                }
                else -> HitType.TOP
            }
        }else if(hitBottom){
            when {
                hitLeft -> {
                    when {
                        brickBelowExists -> HitType.LEFT
                        brickToLeftExists -> HitType.BOTTOM
                        else -> HitType.DIAGONAL_BOTTOM_LEFT
                    }
                }
                hitRight -> {
                    when{
                        brickBelowExists-> HitType.RIGHT
                        brickToRightExists-> HitType.BOTTOM
                        else-> HitType.DIAGONAL_BOTTOM_RIGHT
                    }
                }
                else -> HitType.BOTTOM
            }
        }else if(hitLeft){
            HitType.LEFT
        }else if(hitRight){
            HitType.RIGHT
        }else{
            HitType.ERROR
        }
    }
}