package com.example.imagemanageapp.ui.image

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.content_main.*

class SingleImageFragment : Fragment() {
    private var meta: Meta? = null
    private var token: String? = null
    private var title: String? = null
    private var txtView: TextView? = null
    private var imgView: ImageView? = null
    private var ctx: Context? = null
    private var activity: Activity? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_singleimage, container, false)

        // 프래그먼트의 상위 액티비티 받아오기
        activity = this.requireActivity() as AppCompatActivity

        token = arguments?.getString("token")
        txtView = root.findViewById<TextView>(R.id.titleView)
        imgView = root.findViewById<ImageView>(R.id.imageView)
        ctx = this.context

        loadImage()
        readMeta()

        // 뒤로가기 버튼 눌렀을 때
        val backBtn = root.findViewById<ImageButton>(R.id.backBtn)
        backBtn.setOnClickListener {
            val fragmentManager: FragmentManager = this.parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
            fragmentManager.popBackStack()
        }

        // 메타 정보 버튼 눌렀을 때
        val metaBtn = root.findViewById<ImageButton>(R.id.metaBtn)
        metaBtn.setOnClickListener {
            Toast.makeText(ctx, meta.toString(), Toast.LENGTH_SHORT).show()
        }
        return root
    }

    override fun onStart() {
        super.onStart()

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

    fun readMeta() {
        val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        db.collection("meta")
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

                    meta = Meta(id, title, path, date, latitude, longitude, token)

                    this.title = title
                    txtView!!.text = title

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
    }

    override fun onStop() {
        super.onStop()
        // Toolbar 다시 보이게
        (activity as AppCompatActivity).supportActionBar!!.show()
        // 상태바 다시 보이게
        (activity as AppCompatActivity).window.clearFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // Fragment가 들어가는 layout의 marginTop을 0으로 줌 (Toolbar의 원래 위치까지 채우기 위해)
        val layout: LinearLayout = (activity as AppCompatActivity).linear
        val params: CoordinatorLayout.LayoutParams = CoordinatorLayout.LayoutParams(
            CoordinatorLayout.LayoutParams.MATCH_PARENT,
            CoordinatorLayout.LayoutParams.MATCH_PARENT
        )
        params.topMargin = resources.getDimensionPixelOffset(R.dimen.fragment_margin)
        layout.layoutParams = params
    }
}