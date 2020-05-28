package com.example.imagemanageapp.ui.image

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.imagemanageapp.MainActivity
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
    val month: Int? = null,
    val simpleDate: String? = null
)

// date : 2020.4
// count : 해당 년월에 해당하는 사진 수
data class SimpleDate(
    val date: String? = null,
    var count: Int = 0
)

class ImageFragment : Fragment() {
    private lateinit var imageViewModel: ImageViewModel
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var images = arrayListOf<Image>()
    private var simpleDates = arrayListOf<SimpleDate>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_image, container, false)
        return root
    }

    override fun onStart() {
        super.onStart()

        swipe.setOnRefreshListener {
            readImages()
            swipe.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        (this.activity as MainActivity).autoDelete()
        readImages()

    }

    private fun readImages() {
        images.clear()
        simpleDates.clear()
        db.collection("meta")
            .whereEqualTo("deleted", false)
            /*.orderBy("date", Query.Direction.DESCENDING)*/
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("read img:", "${document.get("title")}")

                    // Long타입
                    val dateLong = document.get("date").toString().toLong()
                    // Date타입
                    val date = Date(dateLong)
                    // String타입
                    val dateStr = DateToString(date)


                    var cal = Calendar.getInstance()
                    cal.time = date
                    val year = cal.get(Calendar.YEAR)
                    val month = cal.get(Calendar.MONTH) + 1
                    val simpleDate = String.format("%s.%s", year.toString(), month.toString())
                    val image =
                        Image(document.get("token").toString(), dateStr, dateLong, year, month, simpleDate)
                    images.add(image)
                    CountSimpleDate(simpleDate)
                }
                images.sortByDescending { image: Image -> image.dateLong}
                showImages()
            }
            .addOnFailureListener { exception ->
                Log.d("read img:", "Error getting documents: ", exception)
            }
    }

    private fun showImages() {
        val mList: ListView = list
        val transaction = parentFragmentManager.beginTransaction()
        simpleDates.reverse()
        val mAdapter = ListAdapter(this.activity, transaction, images, simpleDates)
        mList.adapter = mAdapter
    }

    // Date를 String으로 바꿔주는 함수
    private fun DateToString(date: Date): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val str = dateFormat.format(date)

        return str
    }

    //
    private fun CountSimpleDate(sd : String) {
        var i = 0
        if(simpleDates.isNotEmpty()){
            for(i in 0 until simpleDates.size) {
                // 주어진 sd가 이미 있는 경우
                if(simpleDates[i].date.equals(sd)){
                    simpleDates[i].count++
                    return
                }
            }
        }
        // 주어진 sd가 없거나 맨 처음인 경우
        simpleDates.add(SimpleDate(sd, 1))
        return

    }
}