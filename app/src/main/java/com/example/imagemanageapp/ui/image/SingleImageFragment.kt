package com.example.imagemanageapp.ui.image

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_singleimage.*
import java.util.*

class SingleImageFragment : Fragment() {
    private var meta: Meta? = null
    private var token: String? = null
    private var title: String? = null
    private var txtView: TextView? = null
    private var imgView: ImageView? = null
    private var ctx: Context? = null
    private var activity: Activity? = null
    private var db: FirebaseFirestore? = null
    private var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 상위 액티비티 받아오기
        activity = this.requireActivity() as AppCompatActivity
        root = inflater.inflate(R.layout.fragment_singleimage, container, false)
        token = arguments?.getString("token")
        txtView = root!!.findViewById<TextView>(R.id.titleView)
        imgView = root!!.findViewById<ImageView>(R.id.imageView)
        db = FirebaseFirestore.getInstance()
        ctx = this.context
        return root
    }

    override fun onStart() {
        super.onStart()

        hideBars()

        readMeta()
    }

    private var images = arrayListOf<Meta>()
    fun hideBars() {
        // ToolBar 숨김
        (activity as AppCompatActivity).supportActionBar!!.hide()

        // 상태바 숨김
        (activity as AppCompatActivity).window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // Fragment가 들어가는 layout의 marginTop을 0으로 줌 (Toolbar의 원래 위치까지 채우기 위해)
        val layout: LinearLayout = (activity as AppCompatActivity).linear
        val params: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        )
        params.topMargin = 0
        layout.layoutParams = params
    }

    fun showBars() {
        // Toolbar 다시 보이게
        (activity as AppCompatActivity).supportActionBar!!.show()
        // 상태바 다시 보이게
        (activity as AppCompatActivity).window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // Fragment가 들어가는 layout의 marginTop을 50dp으로 줌 (Toolbar가 들어가기 위해)
        val layout: LinearLayout = (activity as AppCompatActivity).linear
        val params: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        )
        params.topMargin = resources.getDimensionPixelOffset(R.dimen.fragment_margin)
        layout.layoutParams = params
    }

    fun readMeta() {
        db!!.collection("meta")
            .whereEqualTo("token", token)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.get("id").toString()
                    val title = document.get("title").toString()
                    val path = document.get("path").toString()
                    val date = document.get("date").toString().toLong()
                    val latitude = document.get("latitude").toString().toDouble()
                    val longitude = document.get("longitude").toString().toDouble()
                    val token = document.get("token").toString()

                    meta = Meta(id, title, path, date, latitude, longitude, token, false)

                    this.title = title
                    txtView!!.text = title
                    // title이 길면 끝에 ...처리 하기 위함
                    txtView!!.isSingleLine = true
                    txtView!!.maxLines = 1

                    loadImage()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("read img:", "Error getting documents: ", exception)
            }
    }

    private fun loadImage() {
        Glide.with(this.context)
            .load(token)
            .into(imgView)
        buttonActions()
    }

    private fun back() {
        val fragmentManager: FragmentManager = this.parentFragmentManager
        fragmentManager.beginTransaction().remove(this).commit()
        fragmentManager.popBackStack()
    }

    private fun buttonActions() {
        // 뒤로가기 버튼 눌렀을 때
        val backBtn = root!!.findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            back()
        }

        // 메타 정보 버튼 눌렀을 때
        val metaBtn = root!!.findViewById<ImageButton>(R.id.metaBtn)
        metaBtn.setOnClickListener {
            val intent = Intent(activity as AppCompatActivity, PopupActivity::class.java)
            intent.putExtra("datas", meta!!)
            startActivity(intent)
        }

        // edit 버튼 눌렀을 때
        val editBtn = root!!.findViewById<ImageButton>(R.id.editBtn)
        editBtn.setOnClickListener {
            editImage()
        }

        // delete 버튼 눌렀을 때
        val deleteBtn = root!!.findViewById<ImageButton>(R.id.deleteBtn)
        deleteBtn.setOnClickListener {
            val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
            builder.setTitle("삭제 하시겠습니까?")
            builder.setPositiveButton("YES") { dialogInterface, i ->
                deleteImage()
            }.setNegativeButton("NO") { dialogInterface, i ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(ContextCompat.getColor(ctx!!, R.color.colorPrimary))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(ContextCompat.getColor(ctx!!, R.color.colorPrimary))
            }
            dialog.show()

        }

        // share 버튼 눌렀을 때
        val shareBtn = root!!.findViewById<ImageButton>(R.id.shareBtn)
        shareBtn.setOnClickListener {
            shareImage()
        }

    }

    private fun editImage() {

    }

    private fun deleteImage() {
        val docTitle = String.format("%s-%s", meta!!.id, meta!!.title)
        db!!.collection("meta").document(docTitle).update("deleted", true)

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
        showBars()
    }
}