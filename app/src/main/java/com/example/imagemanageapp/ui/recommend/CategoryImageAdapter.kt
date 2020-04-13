package com.example.imagemanageapp.ui.recommend

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R

class CategoryImageAdapter : BaseAdapter{

    //
    data class CategoryImage(var token:String,var name:String)

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
        var view = convertView

        val inflater = LayoutInflater.from(ctx)
        view = inflater.inflate(R.layout.fragment_showcategory_image,parent,false)

        val image = view.findViewById<ImageView>(R.id.img)
        val c = data[position]

        Glide.with(ctx)
            .load(c.token)
            .into(image)




        return view


    }

}