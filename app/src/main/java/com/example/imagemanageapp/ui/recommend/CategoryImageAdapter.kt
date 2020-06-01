package com.example.imagemanageapp.ui.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R
import com.example.imagemanageapp.SearchSingleImageActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_showcategory_image.view.*

class CategoryImageAdapter : BaseAdapter{

    val db = FirebaseFirestore.getInstance()

    val categoryDBList = arrayListOf<String>(
        "similar", "shaken", "darked", "screenshot"
    )
    var tList = ArrayList<String>()
    var tt:String = ""


    private val ctx: Context?
    private val data: ArrayList<CategoryImage>

    constructor(_ctx:Context?,_data:ArrayList<CategoryImage>){
        ctx = _ctx
        data = _data
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var cView = convertView

        val inflater = LayoutInflater.from(ctx)
        cView = inflater.inflate(R.layout.fragment_showcategory_image,parent,false)
        val image = cView.img
        val title = cView.title

        val c = data[position]

        // 이미지 띄우기
        Glide.with(ctx)
            .load(c.token)
            .into(image)

    //    title.text = c.title
        // 클릭 시 사진 확대
        cView.setOnClickListener {
            val intent = Intent(ctx, RecommendSingleImageActivity::class.java)
            intent.putExtra("token", c.token)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx!!.startActivity(intent)
        }

        return cView

    }

    fun getTitleList(pos:Int){

        //position읽어서 해당 데이터 tList로 만들기
        val categoryNum = categoryDBList[pos]
        db.collection("remove")
            .whereEqualTo(categoryNum!!, true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("title").toString()
                    tList.add(name)
                    Log.d("!!!List", tList.toString())
               //    tt = getTlist(tList.toString())

                }


            //    tt = getTlist(tList.toString())
            //    Log.d("???List", tt)



            }

        //return tt


    }

    fun getTlist(pos: Int):String{
        getTitleList(pos)
        Log.d("?!?!List", tt)
        return tList.toString()
    }



}