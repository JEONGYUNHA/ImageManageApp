package com.example.imagemanageapp.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import java.io.File

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })
        val db = FirebaseFirestore.getInstance()
        root.findViewById<Button>(R.id.deleteAll).setOnClickListener {
            db.collection("remove").get().addOnSuccessListener { documents->
                for(d in documents) {
                    d.reference.delete()
                }
            }
            db.collection("auto").get().addOnSuccessListener { documents->
                for(d in documents) {
                    d.reference.delete()
                }
            }
            db.collection("usertag").get().addOnSuccessListener { documents->
                for(d in documents) {
                    d.reference.delete()
                }
            }
            db.collection("meta").get().addOnSuccessListener { documents->
                for(d in documents) {
                    d.reference.delete()
                }
            }
            db.collection("color").get().addOnSuccessListener { documents->
                for(d in documents) {
                    d.reference.delete()
                }
            }
        }
        return root
    }

}
