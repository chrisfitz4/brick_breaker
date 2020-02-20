package com.illicitintelligence.android.brickbreaker

import android.graphics.PointF
import kotlin.math.abs

class Ball {

    private val speedIncrement = 0.1F
    var velocity = PointF(0F,0F)
    var speed = START_VELOCITY
    var radius = 30F
    var position = PointF(0F,0F)

    fun moveBall(){
        position.offset(velocity.x,velocity.y)
    }

    fun checkHit(width: Int, height: Int){
        if (position.x + radius > width) {
            velocity.x = abs(velocity.x) * -1
        }
        if (position.x - radius < 0) {
            velocity.x = abs(velocity.x)
        }
        if (position.y - radius < 0) {
            velocity.y = abs(velocity.y)
        }
        if (position.y - radius > height) {
            velocity.x=0F
            velocity.y=0F
            throw Exception("Game Over")
        }
    }

    fun hitABrick(hitType: HitType){
        speed+=speedIncrement
        when (hitType) {
            HitType.VERTICAL -> velocity.y *= -1
            HitType.HORIZONTAL -> velocity.x *= -1
            HitType.DIAGONAL_TOP_RIGHT -> {
                velocity.x = abs(velocity.x)
                velocity.y = -1 * abs(velocity.y)
            }
            HitType.DIAGONAL_TOP_LEFT -> {
                velocity.x = -1 * abs(velocity.x)
                velocity.y = -1 * abs(velocity.y)
            }
            HitType.DIAGONAL_BOTTOM_LEFT -> {
                velocity.x = -1 * abs(velocity.x)
                velocity.y = abs(velocity.y)
            }
            HitType.DIAGONAL_BOTTOM_RIGHT -> {
                velocity.x = abs(velocity.x)
                velocity.y = abs(velocity.y)
            }
            HitType.ERROR -> {
                throw Exception("HitType Error")
            }
        }
    }

    fun resetVelocity(){
        velocity=PointF(0F,0F)
    }

}