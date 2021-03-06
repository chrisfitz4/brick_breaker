package com.illicitintelligence.android.brickbreaker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.illicitintelligence.android.brickbreaker.R
import com.illicitintelligence.android.brickbreaker.custom.Game
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class MainActivity : AppCompatActivity(), Game.ActivityController {

    private var job: CompletableJob? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val level = resources.getIntArray(R.array.level)
        game.activityController = this
        game.loadLevel(level)
        for(i in 0 until level.size){
            println(Integer.toHexString(level[i]))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        game.job?.cancel()
        game.job=null
        job?.cancel()
        job=null
        IO.cancel()
    }

    override fun showGameOver(lives: Int) {
        job?.cancel()
        job = Job()
        gameStatusTV.visibility= View.VISIBLE
        gameStatusTV.text =
            if (lives == 1) {
                "1 Life Left"
            } else {
                "$lives Lives Left"
            }
        job?.let { myJob ->
            CoroutineScope(myJob + IO).launch {
                delay(1500)
                CoroutineScope(Main).launch {
                    gameStatusTV.visibility=View.GONE
                    game.resetBall()
                    game.gameOverCalledOnce=false
                }
            }
        }
    }
}
