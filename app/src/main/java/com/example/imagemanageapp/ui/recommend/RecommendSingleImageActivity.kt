package com.example.imagemanageapp.ui.recommend

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.PopupActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_singleimage.*
import java.util.*

class RecommendSingleImageActivity : AppCompatActivity() {
    private var meta: Meta? = null
    private var token: String? = null
    private var title: String? = null
    private var txtView: TextView? = null
    private var imgView: ImageView? = null
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_singleimage)

        token = intent.getStringExtra("token")
    }

    override fun onStart() {
        super.onStart()
        hideBars()
        readMeta()
    }

    private var images = arrayListOf<Meta>()
    fun hideBars() {
        // 상태바 숨김
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }
    fun readMeta() {
        db.collection("meta")
            .whereEqualTo("token", token)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("documents", documents.size().toString())
                for (document in documents) {
                    val id = document.get("id").toString()
                    val title = document.get("title").toString()
                    val path = document.get("path").toString()
                    val date = document.get("date").toString().toLong()
                    val latitude = document.get("latitude").toString().toDouble()
                    val longitude = document.get("longitude").toString().toDouble()
                    val token = document.get("token").toString()
                    val upload = document.get("upload").toString().toLong()
                    val place = document.get("place").toString()
                    meta = Meta(id, title, path, date, latitude, longitude, token, false, upload, place)

                    this.title = title
                    titleView.text = title
                    // title이 길면 끝에 ...처리 하기 위함
                    titleView.isSingleLine = true
                    titleView.maxLines = 1

                    loadImage()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("read img:", "Error getting documents: ", exception)
            }
    }

    private fun loadImage() {
        Glide.with(this)
            .load(token).thumbnail(0.5f)
            .into(imageView)
        buttonActions()
    }

    private fun back() {
        finish()
    }

    private fun buttonActions() {
        // 뒤로가기 버튼 눌렀을 때
        val backBtn = findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            back()
        }

        // 메타 정보 버튼 눌렀을 때
        val metaBtn = findViewById<ImageButton>(R.id.metaBtn)
        metaBtn.setOnClickListener {
            val intent = Intent(this, PopupActivity::class.java)
            intent.putExtra("datas", meta!!)
            startActivity(intent)
        }

        // edit 버튼 눌렀을 때
        val editBtn = findViewById<ImageButton>(R.id.editBtn)
        editBtn.setOnClickListener {
            editImage()
        }

        // delete 버튼 눌렀을 때
        val deleteBtn = findViewById<ImageButton>(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("삭제 하시겠습니까?")
            builder.setPositiveButton("YES") { dialogInterface, i ->
                deleteImage()
            }.setNegativeButton("NO") { dialogInterface, i ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            }
            dialog.show()

        }

        // share 버튼 눌렀을 때
        val shareBtn = this.findViewById<ImageButton>(R.id.shareBtn)
        shareBtn.setOnClickListener {
            shareImage()
        }

    }

    private fun editImage() {

    }

    private fun deleteImage() {
        val docTitle = String.format("%s-%s", meta!!.id, meta!!.title)
        db.collection("meta").document(docTitle).update("deleted", true)
        db.collection("auto").document(docTitle).update("deleted", true)
        back()
    }

    private fun shareImage() {
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val uri: Uri = Uri.parse(meta!!.token)
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    override fun onStop() {
        super.onStop()
        //showBars()
    }
}