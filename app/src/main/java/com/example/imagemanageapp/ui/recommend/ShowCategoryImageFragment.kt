
package com.example.imagemanageapp.ui.recommend

import android.annotation.SuppressLint
import android.os.Bundle
import com.example.imagemanageapp.ui.recommend.CategoryImageAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.ListAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.data.view.*
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*


data class CategoryImage(
    val token : String? = null,
    val title : String? = null
)

class ShowCategoryImageFragment: Fragment(){
    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_showcategory_image_list, container, false)
        val aAdapter = CategoryImageAdapter(this.activity,categoryImageData)

        read()

        return root
    }

    private fun read(){
        val titleLIst = arguments?.getString("titleList")
        var titles = titleLIst!!.substring(1,titleLIst.length-1).split(", ")
        Log.d("tList",titles.toString())
        for(t in titles){
            Log.d("t",t)
            db.collection("meta")
                .whereEqualTo("title",t)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
                        Log.d("two","two")
                        val token = document.get("token").toString()
                        Log.d("token",token.toString())
                        categoryImageData.add(CategoryImage(token,t))
                        Log.d("dataClass",categoryImageData.toString())
                    }
                    upload()

                }
                .addOnFailureListener { exception ->
                    Log.d("read failed:", "Error getting documents: ", exception)
                }
        }

    }


    private fun upload(){
        val cGrid = grid
        val cAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        cGrid.adapter = cAdapter
    }

}



