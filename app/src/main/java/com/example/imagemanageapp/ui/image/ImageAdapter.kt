package com.example.imagemanageapp.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import kotlinx.android.synthetic.main.data.view.*

class ImageAdapter : BaseAdapter {
    private val ctx: Context?
    private val data: ArrayList<Image>

    constructor(_ctx: Context?, _data: ArrayList<Image>) {
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
        var mView = convertView
        val inflater = LayoutInflater.from(ctx)
        mView = inflater.inflate(R.layout.data, parent, false)

        val image = mView.img

        val d = data[position]

        // 이미지 띄워주기
        Glide.with(ctx)
            .load(d.token)
            .into(image)

        return mView
    }


}