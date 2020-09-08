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
        cView = inflater.inflate(R.layout.data,parent,false)
        val image = cView.findViewById<ImageView>(R.id.img)

        val c = data[position]

        // 이미지 띄우기
        Glide.with(ctx)
            .load(c.token).thumbnail(0.1f)
            .centerCrop()
            .into(image)


     return cView

    }

    fun giveCtx():Context?{
        return ctx
    }



}