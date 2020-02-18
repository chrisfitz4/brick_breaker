package com.illicitintelligence.android.brickbreaker

import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF

enum class PaddleProperty {
    NORMAL,
    SPECIAL
}

data class Paddle(var rect: RectF, var color: Int, var paddleProperty: PaddleProperty) {
    fun hitPaddle(point: PointF, radius: Float, velocityX: Float, velocityY: Float): PointF {
        if(isHit(point,radius)){
            return PointF(velocityX,-1*velocityY)
        }else{
            return PointF(velocityX,velocityY)
        }
    }
    fun isHit(point: PointF, radius: Float): Boolean{
        return rect.left<point.x-radius- PADDLE_OFFSET&&rect.right+ PADDLE_OFFSET>point.x+radius&&point.y<rect.top&&point.y+radius>rect.top
    }
    fun getWidth(): Float{
        return rect.width()
    }
}