package com.example.imagemanageapp.ui.album

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.Image
import com.example.imagemanageapp.ui.image.ListAdapter
import com.example.imagemanageapp.ui.image.SingleImageFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.text.SimpleDateFormat
import java.util.*

class AlbumFragment : Fragment() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var datas = arrayListOf<Album>()
    private var root: View? = null
    private var tags: HashMap<String, String> = hashMapOf(
        "사람" to "person",
        "동물" to "animal",
        "교통수단" to "traffic",
        "가구" to "furniture",
        "책" to "book",
        "가방" to "bag",
        "스포츠" to "sport",
        "전자기기" to "device",
        "식물" to "plant",
        "음식" to "food",
        "잡동사니" to "things"
    )
    private var koreanTags: ArrayList<String> = arrayListOf(
        "사람", "동물",
        "교통수단",
        "가구",
        "책",
        "가방",
        "스포츠",
        "전자기기",
        "식물",
        "음식",
        "잡동사니"
    )
    private var englishTags: ArrayList<String> = arrayListOf(
        "person", "animal",
        "traffic",
        "furniture",
        "book",
        "bag",
        "sport",
        "device",
        "plant",
        "food",
        "things"
    )

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
        datas.clear()
        readImages()
    }

    private fun readImages(){
        for (i in 0..englishTags.size - 1) {
            var size = 0
            var token: String
            db.collection("auto")
                .whereEqualTo(englishTags[i], true)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.size() != 0) {
                        var title = documents.documents[documents.size()-1].get("title").toString()
                        db.collection("meta").whereEqualTo("title", title).get().addOnSuccessListener {
                            size = documents.size()
                            token = it.documents[0].get("token").toString()
                            datas.add(Album(koreanTags[i], englishTags[i], size, token))
                            Log.d("datas", datas.toString())
                            setAdapter()
                        }
                    }
                }
        }
    }

    private fun setAdapter() {
        val transaction = parentFragmentManager.beginTransaction()
        val mAdapter = AlbumGridAdapter(this.activity, transaction, datas)
        albumGridView.adapter = mAdapter
    }

}