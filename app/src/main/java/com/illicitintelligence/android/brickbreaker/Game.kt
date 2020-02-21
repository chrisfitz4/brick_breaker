package com.illicitintelligence.android.brickbreaker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.math.sqrt
import kotlin.random.Random

class Game(context: Context, attributeSet: AttributeSet) : View(context, attributeSet), View.OnTouchListener, Paddle.PaddleDelegate, Bricks.TrappedBallDelegate {

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
    private val ball = Ball()
    private var ballStartY: Float = 0F
    private lateinit var bricks: Bricks
    private lateinit var paddle: Paddle
    private lateinit var level: IntArray
    private var winner = false
    private var restartable = false

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bricks = Bricks(w, h / 3, this)
        bricks.createRectanglesLevel(level)
        //bricks.createRectangles()
        paddle = Paddle(
            RectF(
                w / 2 - PADDLE_WIDTH / 2,
                8 * h / 9.toFloat(),
                w / 2 + PADDLE_WIDTH / 2,
                8 * h / 9 + PADDLE_HEIGHT
            ),
            Color.YELLOW,
            PaddleProperty.NORMAL,
            this
        )
        ballStartY=paddle.rect.top-ball.radius+1
        ball.position=PointF(w / 2.toFloat()+BALL_OFFSET,ballStartY)
        paddle.paddleX = w/2.toFloat()
        job = CoroutineScope(IO).launch {
            while(true){
                delay(15)
                CoroutineScope(Main).launch{
                    paddle.moveTowardX(paddle.paddleX,width)
                    if(startable||restartable){
                        ball.position.x=paddle.getCenter()+ BALL_OFFSET
                    }
                }
            }
        }
    }

    fun loadLevel(level: IntArray){
        this.level = level
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
        if(winner){
            gameWonMove()
        }else if(restartable){

        }else{
            moveBall()
        }
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
            val brickRow = it.row
            val brickColumn = it.column
            ball.hitABrick(it.hitType(ball.position,
                bricks.getBrick(brickRow,brickColumn-1),
                bricks.getBrick(brickRow,brickColumn+1),
                bricks.getBrick(brickRow-1,brickColumn),
                bricks.getBrick(brickRow+1,brickColumn)))
            if(bricks.bricksLeftCount==0){
                winner=true
                paddle.paddleX=paddle.getCenter()
                val deltaX = paddle.paddleX-ball.position.x
                val deltaY = paddle.rect.top-ball.position.y
                val magnitude = sqrt(deltaX*deltaX+deltaY*deltaY)
                ball.velocity.x=deltaX/magnitude*WIN_SPEED
                ball.velocity.y=deltaY/magnitude*WIN_SPEED
            }
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

    private fun gameWonMove(){
        ball.moveBall()
        paddle.winHitPaddle(ball)
        invalidate()
    }

    fun resetBall(){
        ball.position=PointF(ball.position.x,ballStartY)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        event?.let {
            if (!winner) {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        timeClick()
                        paddle.paddleX = it.x
                    }
                    MotionEvent.ACTION_UP -> {
                        if (startable && withinTime) {
                            performClick()
                        }
                        paddle.paddleX = paddle.getCenter()
                    }
                    MotionEvent.ACTION_MOVE -> paddle.paddleX = it.x
                    else -> {
                    }
                }
            }
        }
        return true
    }

    private fun timeClick() {
        withinTime = true
        CoroutineScope(IO).launch {
            delay(150)
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

    override fun bricksHitReset() {
        bricks.timesInARow=0
    }

    override fun hitPaddleAfterWin() {
        winner=false
        restartable=true
    }

    override fun fixVelocity() {
        if(ball.velocity.x>=0){
            ball.velocity.x+=0.1F
        }else{
            ball.velocity.x-=0.1F
        }
        if(ball.velocity.y>0){
            ball.velocity.y+= Random.nextInt(4)/10F
        }
    }
}