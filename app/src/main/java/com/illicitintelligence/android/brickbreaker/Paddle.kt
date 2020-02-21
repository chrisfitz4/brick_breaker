package com.illicitintelligence.android.brickbreaker

import android.graphics.PointF
import android.graphics.RectF
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

enum class PaddleProperty {
    NORMAL,
    SPECIAL
}

class Paddle(var rect: RectF, var color: Int, var paddleProperty: PaddleProperty, val paddleDelegate: PaddleDelegate) {

    var paddleX = 0F

    interface PaddleDelegate{
        fun bricksHitReset()
        fun hitPaddleAfterWin()
    }

    fun winHitPaddle(ball: Ball) {
        if(isHit(ball.position,ball.radius)){
            ball.velocity=PointF(0F,0F)
            paddleDelegate.hitPaddleAfterWin()
        }
    }

    fun hitPaddle(point: PointF, radius: Float, velocity: PointF, speed: Float): PointF {
        return if(isHit(point,radius)){
            paddleDelegate.bricksHitReset()
            val angle = calculateAngle(point)
            val xVel = cos(angle)
            val yVel = sin(angle)
            PointF(xVel*speed,-1*yVel*speed)
        }else{
            velocity
        }
    }

    private fun isHit(point: PointF, radius: Float): Boolean{
        return rect.left<point.x+radius-PADDLE_OFFSET   //within the left side
                &&rect.right>point.x-radius+PADDLE_OFFSET //within the right side
                &&point.y<rect.top  //above the paddle
                &&point.y+radius>rect.top //not too far above the paddle
    }

    fun getWidth(): Float{
        return rect.width()
    }

    fun getCenter(): Float{
        return (rect.right+rect.left)/2
    }

    fun moveTowardX(to: Float,max: Int){
        val dif = to-(rect.right+rect.left)/2
        val posOrNeg = dif/abs(dif)
        if((posOrNeg==1F&&rect.right>=max)||(posOrNeg==-1F&&rect.left<=0)){
            //do nothing
            return
        }else if(abs(dif)< PADDLE_VELOCITY){
            rect.left+=dif
            rect.right+=dif
        }else{
            rect.left+= PADDLE_VELOCITY*posOrNeg
            rect.right+= PADDLE_VELOCITY*posOrNeg
        }
    }

    private fun calculateAngle(point: PointF): Float{
        var percent = (point.x-rect.left)/getWidth()*100
        if(percent>99F){
            percent=99F
        }else if(percent<1F){
            percent=1F
        }
        val relativeDistFromCenter = (percent-50)/50
        return acos(relativeDistFromCenter)
    }
}