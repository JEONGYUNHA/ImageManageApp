package com.example.imagemanageapp.ui.image

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_image.*
import java.text.SimpleDateFormat
import java.util.*

data class Image(
    val token: String? = null,
    val dateStr: String? = null,
    val dateLong: Long? = null,
    val year: Int? = null,
    val month: Int? = null
)

class ImageFragment : Fragment() {
    private lateinit var imageViewModel: ImageViewModel
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var images = arrayListOf<Image>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        imageViewModel =
            ViewModelProviders.of(this).get(ImageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_image, container, false)
        /*val textView: TextView = root.findViewById(R.id.text_image)
        imageViewModel.text.observe(this, Observer {
            textView.text = it
        })*/
        readImages()

        return root
    }

    private fun readImages() {
        db.collection("meta").orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("read img:", "${document.get("title")}")

                    // Long타입
                    val dateLong = document.get("date").toString().toLong()
                    // Date타입
                    val date = Date(dateLong)
                    // String타입
                    val dateStr = date.toString()

                    val year = date.year
                    val month = date.month
                    val image =
                        Image(document.get("token").toString(), dateStr, dateLong, year, month)
                    images.add(image)
                    Log.d("aaa", image.toString())
                }
                showImages()
            }
            .addOnFailureListener { exception ->
                Log.d("read img:", "Error getting documents: ", exception)
            }
    }

    private fun showImages() {
        val mGrid: GridView = grid
        val mAdapter = ImageAdapter(this.activity, images)
        mGrid.adapter = mAdapter
        // 각 메뉴별 클릭 시 이벤트 달기
        mGrid.setOnItemClickListener { parent, view, position, id ->

        }
    }

    private fun StringToDate(str: String): Date {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = dateFormat.parse(str)

        return date
    }


}