package com.example.imagemanageapp.ui.recommend

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recommend_secondfragment.*


class RecommendForthFragment : Fragment() {

    private val categoryImageData = arrayListOf<CategoryImage>()
    private val db = FirebaseFirestore.getInstance()
    private var cGrid : GridView? = grid
   // private var cAdapter : CategoryImageAdapter? = null
    private var img : ImageView? = null
    var tList = ArrayList<String>()

    // 중복 띄우기 방지, 한 번 읽으면 true로 변경
    private var readSucess  = false

    //fragment 시작하면 불리는 함수
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_recommend_forthfragment, container, false)


        img = root.findViewById((R.id.img))


        //툴바 메뉴사용하려면 꼭 필요
        setHasOptionsMenu(true)

        getTitleList()

        return root
    }

    private fun getTitleList() {

        db.collection("remove")
            .whereEqualTo("screenshot", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("title").toString()
                    if (!readSucess) {
                        tList.add(name)
                        Log.d("!!!List", tList.toString())
                    }

                }
                if (!readSucess) {
                    read(tList.toString())
                }
                upload()

                readSucess = true

            }
            .addOnFailureListener {
            }



    }

    private fun read(tList:String){
        val titleLIst = tList
        var titles = titleLIst!!.substring(1,titleLIst.length-1).split(", ")
        Log.d("tttList",titles.toString())
        for(t in titles){
            Log.d("t",t)
            db.collection("meta")
                .whereEqualTo("deleted",false)
                .whereEqualTo("title",t)
                .get()
                .addOnSuccessListener { documents ->
                    for(document in documents) {
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
        val color= Color.GRAY
        val mode = PorterDuff.Mode.SCREEN
        var num=0

        cGrid = grid
        val cAdapter = CategoryImageAdapter(this.activity,categoryImageData)
        cGrid!!.adapter = cAdapter



    }

}