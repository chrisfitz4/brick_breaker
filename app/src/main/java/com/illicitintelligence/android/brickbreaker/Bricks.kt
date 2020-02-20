package com.illicitintelligence.android.brickbreaker

import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect

class Bricks(width: Int, height: Int) {

    private val brickWidth = width / BRICK_COUNT
    private val brickHeight = height / BRICK_COUNT_VERT
    var bricksList = ArrayList<Brick?>()

    fun createRectangles() {
        var startX = 0
        var startY = 0
        for (i in 0 until BRICK_COUNT_VERT) {
            for (j in 0 until BRICK_COUNT) {
                bricksList.add(
                    Brick(
                        Rect(
                            startX,
                            startY,
                            startX + brickWidth,
                            startY + brickHeight
                        ), Color.RED, Property.NORMAL
                    )
                )
                startX += brickWidth
            }
            startX = 0
            startY += brickHeight + SPACE_BETWEEN_BRICKS
        }
    }

    fun didItHitABrick(position: PointF, radius: Float): Brick? {
        val x = position.x
        val y = position.y
        for (item in bricksList) {
            item?.let { brick ->
                if (x + radius > brick.rect.left
                    && x - radius < brick.rect.right
                    && y + radius > brick.rect.top
                    && y - radius < brick.rect.bottom
                ) {
                    hit(item)
                    return brick
                }
            }
        }
        return null
    }

    private fun hit(item: Brick) {
        bricksList.remove(item)
    }
}