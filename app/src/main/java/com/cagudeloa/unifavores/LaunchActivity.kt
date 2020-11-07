package com.cagudeloa.unifavores

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
    }

    fun launchMemoryGames(view: View) {
        val intent = Intent(this, MemoryGamesMainActivity::class.java)
        startActivity(intent)
    }
    fun launchFavorsApp(view: View) {
        val intent = Intent(this, UniFavoresMainActivity::class.java)
        startActivity(intent)
    }
}