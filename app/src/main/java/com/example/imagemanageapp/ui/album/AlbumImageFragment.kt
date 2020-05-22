package com.example.imagemanageapp.ui.album

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
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.album_row.*
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.android.synthetic.main.fragment_albumimage.*
import kotlinx.android.synthetic.main.fragment_image.*
import java.text.SimpleDateFormat
import java.util.*

data class AlbumImage (
    var token : String? = null,
    var date : Long = 0
)
class AlbumImageFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var tokens = arrayListOf<AlbumImage>()
    private var root : View? = null
    private var krTag : String? = null
    private var enTag : String? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //imageViewModel = ViewModelProviders.of(this).get(ImageViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_albumimage, container, false)
        krTag = arguments?.getString("krTag")
        enTag = arguments?.getString("enTag")
        return root
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        readImages()
        root!!.findViewById<TextView>(R.id.titleView).text = krTag!!

    }

    private fun readImages() {
        tokens.clear()
        db.collection("auto")
            .whereEqualTo(enTag!!, true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    db.collection("meta").document(docTitle).get().addOnSuccessListener {
                        val token = it.get("token").toString()
                        var date = it.get("date").toString().toLong()
                        tokens.add(AlbumImage(token, date))
                        if(documents.size() == tokens.size)
                            showImages()
                    }
                }
            }

    }

    private fun showImages() {
        val mGrid : GridView = gridView
        val transaction = parentFragmentManager.beginTransaction()
        tokens.sortByDescending { data: AlbumImage -> data.date }
        val mAdapter = AlbumImageGridAdapter(this.activity, transaction, krTag!!, enTag!!, tokens)
        mGrid.adapter = mAdapter
    }
}