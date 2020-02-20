package com.illicitintelligence.android.brickbreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class Game(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), View.OnTouchListener {

    interface ActivityController{
        fun showGameOver(lives: Int)
    }
    var activityController: ActivityController? = null
    private var lives = 3
    init {
        this.setOnTouchListener(this)
    }
    var gameOverCalledOnce=false
    private var withinTime = false
    private var startable = true
    var job: Job? = null
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.BLACK
    }
    private val ballPaint = Paint().apply {
        color = Color.GRAY
    }
    private val paddlePaint = Paint().apply {
        color = Color.GREEN
    }
    private var paddleX = 0F
    private val ball = Ball()
    private var ballStartY: Float = 0F
    private lateinit var bricks: Bricks
    private lateinit var paddle: Paddle

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bricks = Bricks(w, h / 3)
        bricks.createRectangles()
        ballStartY=7 * h / 8.toFloat()
        ball.position=PointF(w / 2.toFloat()+BALL_OFFSET,ballStartY)
        paddle = Paddle(
            RectF(
                w / 2 - PADDLE_WIDTH / 2,
                8 * h / 9.toFloat(),
                w / 2 + PADDLE_WIDTH / 2,
                8 * h / 9 + PADDLE_HEIGHT
            ),
            Color.YELLOW,
            PaddleProperty.NORMAL
        )
        paddleX = w/2.toFloat()
        job = CoroutineScope(IO).launch {
            while(true){
                delay(15)
                CoroutineScope(Main).launch{
                    paddle.moveTowardX(paddleX,width)
                    if(startable){
                        ball.position.x=paddle.getCenter()+ BALL_OFFSET
                    }
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (item in bricks.bricksList) {
            item?.let {
                paint.color = it.color
                canvas?.drawRect(it.rect, paint)
                canvas?.drawRect(it.rect, strokePaint)
            }
        }
        canvas?.drawCircle(ball.position.x,ball.position.y,ball.radius,ballPaint)
        canvas?.drawRoundRect(paddle.rect, paddle.getWidth(), paddle.getWidth(), paddlePaint)
        moveBall()
    }

    private fun moveBall() {
        ball.moveBall()
        try {
            ball.checkHit(width,height)
        }catch(exception: Exception){
            if(!gameOverCalledOnce) {
                gameOverCalledOnce=true
                gameOver()
            }
        }
        val brick = bricks.didItHitABrick(ball.position, ball.radius)
        brick?.let {
            ball.hitABrick(it.hitType(ball.position))
        }
        if(!startable) {
            ball.velocity=paddle.hitPaddle(ball.position,ball.radius,ball.velocity,ball.speed)
        }
        invalidate()
    }

    private fun gameOver() {
        lives--
        activityController?.showGameOver(lives)
        startable = true
        ball.resetVelocity()
    }

    fun resetBall(){
        ball.position=PointF(ball.position.x,ballStartY)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    timeClick()
                    paddleX=it.x
                }
                MotionEvent.ACTION_UP -> {
                    if (startable && withinTime) {
                        performClick()
                    }
                    paddleX=paddle.getCenter()
                }
                MotionEvent.ACTION_MOVE -> paddleX=it.x
                else -> {
                }
            }
        }
        return true
    }

    private fun timeClick() {
        withinTime = true
        CoroutineScope(IO).launch {
            delay(200)
            CoroutineScope(Main).launch {
                withinTime = false
            }
        }
    }

    override fun performClick(): Boolean {
        moveBall()
        startable = false
        return super.performClick()
    }
}