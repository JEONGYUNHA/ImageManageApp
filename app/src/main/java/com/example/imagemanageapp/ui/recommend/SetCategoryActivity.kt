package com.example.imagemanageapp.ui.recommend

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.imagemanageapp.MainActivity
import com.example.imagemanageapp.R
import kotlinx.android.synthetic.main.activity_loading.*

class SetCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        btn.setOnClickListener { startApp() }
    }

    // Main으로 전환
    fun startApp() {
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}