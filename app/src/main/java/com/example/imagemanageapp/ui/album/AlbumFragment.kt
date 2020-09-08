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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.imagemanageapp.MainActivity
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.Image
import com.example.imagemanageapp.ui.image.ListAdapter
import com.example.imagemanageapp.ui.image.SingleImageFragment
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.text.SimpleDateFormat
import java.util.*

const val TAG_NUMBER: Int = 11

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
    private var count = 0
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
        count = 0
        //(this.activity as MainActivity).autoDelete()
        readAll()
        readImages()
    }

    private fun readAll() {
        db.collection("meta").whereEqualTo("deleted", false).get().addOnSuccessListener { documents ->
            var size = documents.size()
            var count = 0
            var token = ""
            var max : Long = 0
            for(d in documents) {
                count += 1
                if(d.get("date").toString().toLong() > max) {
                    max = d.get("date").toString().toLong()
                    token = d.get("token").toString()
                }
                if(count == size)
                    datas.add(Album(null, null, size, token))
            }

        }
    }

    private fun readImages() {
        for (i in 0..TAG_NUMBER - 1) {
            readAuto(i)
        }
    }

    private fun readAuto(i: Int) {
        var titleList: ArrayList<String> = arrayListOf()
        db.collection("auto")
            .whereEqualTo(englishTags[i], true)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty)
                    count += 1
                for (d in documents) {
                    val bool = d.get("deleted").toString().toBoolean()
                    if (!bool) {
                        val title = d.get("title").toString()
                        val id = d.get("id").toString()
                        val docTitle = String.format("%s-%s", id, title)
                        titleList.add(docTitle)
                    }
                    if (documents.documents[documents.size() - 1].reference == d.reference)
                        readMeta(titleList, i)
                }
            }
    }

    private fun readMeta(titleList: ArrayList<String>, i: Int) {
        count += 1
        val size = titleList.size
        var token = ""
        val title = titleList[size-1]
        db.collection("meta").document(title).get().addOnSuccessListener {
            token = it.get("token").toString()
            datas.add(Album(koreanTags[i], englishTags[i], size, token))
            if (i == TAG_NUMBER - 1) {
                setAdapter()
            }
            else if (count == TAG_NUMBER) {
                setAdapter()
            }
        }

    }

    private fun setAdapter() {
        Log.d("setAdapter", datas.toString())
        val mGrid: GridView = albumGridView
        val transaction = parentFragmentManager.beginTransaction()
        val mAdapter = AlbumGridAdapter(this.activity, transaction, datas)
        mGrid.adapter = mAdapter
    }

}