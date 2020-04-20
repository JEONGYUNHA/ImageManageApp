package com.example.imagemanageapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.FileObserver
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_loading.*


class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        btn.setOnClickListener { startApp() }
    }

    // Main으로 전환
    fun startApp() {
        val intent = Intent(baseContext, MainActivity::class.java)
        /*val intent = Intent(baseContext, OpenCV::class.java)*/
        startActivity(intent)
        finish()
    }
}