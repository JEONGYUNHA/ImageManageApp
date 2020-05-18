package com.example.imagemanageapp.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.Image
import com.example.imagemanageapp.ui.image.ListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.text.SimpleDateFormat
import java.util.*

class AlbumFragment : Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var tokens = arrayListOf<String>()
    private var root : View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_album, container, false)


        return root
    }

    override fun onResume() {
        super.onResume()
        readImages()

    }

    private fun readImages() {
        tokens.clear()
        db.collection("auto")
            .whereEqualTo("person", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    db.collection("meta").document(docTitle).get().addOnSuccessListener {
                        val token = it.get("token").toString()
                        tokens.add(token)
                        Log.d("albumToken", tokens.toString())
                    }
                }
                showImages()
            }
    }

    private fun showImages() {
        val mGrid : GridView = gridView
        val transaction = parentFragmentManager.beginTransaction()
        val mAdapter = AlbumGridAdapter(this.activity, transaction, tokens)
        mGrid.adapter = mAdapter
        Log.d("tokensSize", tokens.size.toString())
        // GridView의 numColumns를 불러온 이미지 갯수로 지정해주기
        root!!.findViewById<GridView>(R.id.gridView).numColumns = tokens.size
    }

    // Date를 String으로 바꿔주는 함수
    private fun DateToString(date: Date): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val str = dateFormat.format(date)

        return str
    }


}