package com.isabella.nearbypet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val handle = Handler()
        handle.postDelayed(Runnable {
            showHome() },
            4000)
    }

    private fun showHome() {
        val intent = Intent(this@SplashScreenActivity, MapsActivity::class.java)
        startActivity(intent)
        finish()
    }
}