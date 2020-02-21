package com.illicitintelligence.android.brickbreaker.custom.components

import android.graphics.Color
import android.graphics.PointF
import android.graphics.Rect
import android.util.Log
import com.illicitintelligence.android.brickbreaker.util.BRICK_COUNT
import com.illicitintelligence.android.brickbreaker.util.BRICK_COUNT_VERT
import com.illicitintelligence.android.brickbreaker.util.SPACE_BETWEEN_BRICKS
import java.lang.IndexOutOfBoundsException

class Bricks(width: Int, height: Int, private val trappedBallDelegate: TrappedBallDelegate) {

    interface TrappedBallDelegate{
        fun fixVelocity()
    }
    private val brickWidth = width / BRICK_COUNT
    private val brickHeight = height / BRICK_COUNT_VERT
    var bricksList = ArrayList<Brick?>()
    private var bricksStillAroundList = Array(BRICK_COUNT){ Array(
        BRICK_COUNT_VERT
    ){true} }
    var bricksLeftCount = 0
    var timesInARow = 0

    /*fun createRectangles() {
        bricksList = ArrayList()
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
                        ), Color.RED, Property.NORMAL,
                        i,
                        j
                    )
                )
                startX += brickWidth
            }
            startX = 0
            startY += brickHeight + SPACE_BETWEEN_BRICKS
        }
    }*/

    fun createRectanglesLevel(levelInput: IntArray) {
        bricksList = ArrayList()
        var startX = 0
        var startY = 0
        for (i in 0 until BRICK_COUNT_VERT) {
            for (j in 0 until BRICK_COUNT) {
                if(levelInput[BRICK_COUNT *i+j]==-1){
                    //empty space/null brick
                    bricksStillAroundList[j][i]=false
                    startX += brickWidth
                    Log.d("TAG_X","-1")
                    continue
                }
                bricksList.add(
                    Brick(
                        Rect(
                            startX,
                            startY,
                            startX + brickWidth,
                            startY + brickHeight
                        ),
                        if (levelInput[BRICK_COUNT * i + j] == 0) {
                            Color.GRAY
                        } else {
                            var hexVal = Integer.toHexString(levelInput[BRICK_COUNT * i + j])
                            while (hexVal.length < 6) {
                                hexVal = "0$hexVal"
                            }
                            hexVal = "#$hexVal"
                            try {
                                Color.parseColor(hexVal)
                            } catch (exception: IllegalArgumentException) {
                                Log.d("TAG_X", hexVal)
                                Color.YELLOW
                            }
                        },
                        if (levelInput[BRICK_COUNT * i + j] == 0) {
                            Property.UNBREAKABLE
                        } else {
                            bricksLeftCount++
                            Property.NORMAL
                        },
                        i,
                        j
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
        when {
            item.property!= Property.UNBREAKABLE -> {
                timesInARow=0
                bricksList.remove(item)
                bricksStillAroundList[item.row][item.column] = false
                bricksLeftCount--
            }
            else -> {
                timesInARow++
                if(timesInARow>4){
                    trappedBallDelegate.fixVelocity()
                }
            }
        }
    }

    fun getBrick(row: Int, column: Int): Boolean{
        return try{
            bricksStillAroundList[row][column]
        }catch(exception: IndexOutOfBoundsException){
            false
        }
    }
}