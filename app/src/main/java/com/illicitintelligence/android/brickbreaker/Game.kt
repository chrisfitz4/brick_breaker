package com.illicitintelligence.android.brickbreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.abs

class Game(context: Context, attributeSet: AttributeSet): View(context,attributeSet), View.OnClickListener{

    lateinit var bricks: Bricks
    val paint = Paint().apply{
        style=Paint.Style.FILL
    }
    val strokePaint = Paint().apply {
        style=Paint.Style.STROKE
        strokeWidth=2f
        color= Color.BLACK
    }
    val ballPaint = Paint().apply{
        color=Color.GRAY
    }
    val paddlePaint = Paint().apply{
        color = Color.GREEN
    }
    var ballPosition : PointF = PointF()
    val BALL_RADIUS = 30F
    var velocityX = 0F
    var velocityY = 0F
    init {
        this.setOnClickListener(this)
    }
    lateinit var paddle: Paddle


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bricks = Bricks(w,h/3)
        bricks.createRectangles()
        ballPosition.x = w/2.toFloat()
        ballPosition.y = 7*h/8.toFloat()
        paddle = Paddle(RectF(w/2- PADDLE_WIDTH/2,8*h/9.toFloat(),w/2+ PADDLE_WIDTH/2,8*h/9+ PADDLE_HEIGHT),Color.YELLOW,PaddleProperty.NORMAL)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for(item in bricks.bricksList){
            item?.let{
                paint.color=it.color
                canvas?.drawRect(it.rect,paint)
                canvas?.drawRect(it.rect,strokePaint)
            }
        }
        canvas?.drawCircle(ballPosition.x,ballPosition.y,BALL_RADIUS,ballPaint)
        canvas?.drawRoundRect(paddle.rect,paddle.getWidth(),paddle.getWidth(),paddlePaint)
        moveBall()
    }

    private fun moveBall(){
        ballPosition.offset(velocityX,velocityY)
        if(ballPosition.x+BALL_RADIUS>width){
            velocityX= abs(velocityX) *-1
        }
        if(ballPosition.x-BALL_RADIUS<0){
            velocityX= abs(velocityX)
        }
        if(ballPosition.y-BALL_RADIUS<0){
            velocityY=abs(velocityY)
        }
        if(ballPosition.y+BALL_RADIUS>height){
            gameOver()
        }
        val brick = bricks.didItHitABrick(ballPosition,BALL_RADIUS)
        brick?.let {
            when(it.hitType(ballPosition)){
                HitType.VERTICAL->velocityY*=-1
                HitType.HORIZONTAL->velocityX*=-1
                HitType.DIAGONAL->{velocityX*=-1
                    velocityY*=-1}
            }
        }
        val newVelocityPoint = paddle.hitPaddle(ballPosition,BALL_RADIUS,velocityX,velocityY)
        velocityX = newVelocityPoint.x
        velocityY = newVelocityPoint.y
        invalidate()
    }

    override fun onClick(v: View?) {
        velocityX=5F
        velocityY=-5F
        moveBall()
    }

    private fun gameOver() {

    }

}