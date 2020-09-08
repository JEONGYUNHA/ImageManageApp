package com.example.imagemanageapp.ui.trash

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.MainActivity
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.trash.GridAdapter
import com.example.imagemanageapp.ui.image.Image
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_trash.*
import kotlinx.android.synthetic.main.fragment_trash.swipe

class TrashFragment : Fragment() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var images = arrayListOf<Image>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_trash, container, false)
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
        //(this.activity as MainActivity).autoDelete()
        readImages()
    }

    private fun readImages() {
        images.clear()
        db.collection("meta")
            .whereEqualTo("deleted", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("read img:", "${document.get("title")}")
                    val image =
                        Image(document.get("token").toString(), null, document.get("date").toString().toLong(), null, null, null)
                    images.add(image)
                }
                images.sortByDescending { image: Image -> image.dateLong}
                showImages()
            }
            .addOnFailureListener { exception ->
                Log.d("read img:", "Error getting documents: ", exception)
            }
    }

    private fun showImages() {
        val transaction = parentFragmentManager.beginTransaction()
        val mAdapter = GridAdapter(this.activity, transaction, images)
        mGrid.adapter = mAdapter
    }
}