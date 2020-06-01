package com.example.imagemanageapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide

class SearchImageGridAdapter : BaseAdapter {
    private val ctx: Context?
    private val data: ArrayList<SearchImageCategory>

    constructor(
        _ctx: Context?,
        _data: ArrayList<SearchImageCategory>
    ) {
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

    @SuppressLint("ViewHolder", "LongLogTag")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var mView = convertView
        val inflater = LayoutInflater.from(ctx)
        mView = inflater.inflate(R.layout.search_data_xml, parent, false)

        val imageView = mView.findViewById<ImageView>(R.id.image)
        //val imageView = mView.image
        val token = data[position].token

        Log.d("check adapter token data", token)

        // 이미지 띄워주기
        Glide.with(ctx)
            .load(token).thumbnail(0.1f)
            .centerCrop()
            .into(imageView)

        // 클릭 시 사진 확대
        mView.setOnClickListener {
            val intent = Intent(ctx, SearchSingleImageActivity::class.java)
            intent.putExtra("token", token)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx!!.startActivity(intent)
        }
        return mView
    }
}