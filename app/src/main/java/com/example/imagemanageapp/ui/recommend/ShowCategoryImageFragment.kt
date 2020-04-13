package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_image.*

class ShowCategoryImageFragment: Fragment(){
    private val categoryImageData = arrayListOf<CategoryImageAdapter.CategoryImage>()
    private val db = FirebaseFirestore.getInstance()
    private var readSucess = false
    val categoryNum = arguments?.getString("categoryNum")
   // Log.d("categoryNum",categoryNum)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_showcategory_image, container, false)
        val aAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        val categoryNum = arguments?.getString("categoryNum")
        Log.d("categoryNum",categoryNum)

        read()

        return root
    }

    private fun read(){
        val categoryNum = arguments?.getString("titleList")
        Log.d("category2",categoryNum)
        db.collection("remove")
            .whereEqualTo(categoryNum!!,true)
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                   val name = document.get("title").toString()
                    db.collection("meta")
                        .whereEqualTo("title",name)
                        .get()
                        .addOnSuccessListener { document ->
                            for (document in documents) {
                                val token = document.get("token").toString()
                                val token2 = "tokwn"
                                categoryImageData.add(
                                    CategoryImageAdapter.CategoryImage(
                                        token,
                                        name
                                    )
                                )

                            }

                        }
                  //  Log.d("token2",token.toString())
                Log.d("title",name)

                }
                upload()
                readSucess = true
            }
            .addOnFailureListener{
                Log.d("failUpload","")
            }
    }

    private fun upload(){
        val cGrid = grid
        val cAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        cGrid.adapter = cAdapter
    }

}