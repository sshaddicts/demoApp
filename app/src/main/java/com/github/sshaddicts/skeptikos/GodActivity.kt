package com.github.sshaddicts.skeptikos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

class GodActivity : AppCompatActivity() {

    private var fm: FragmentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppThemeDark)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        fm = supportFragmentManager
    }

    companion object {
        private val TAG = GodActivity::class.java.toString()
    }
}
