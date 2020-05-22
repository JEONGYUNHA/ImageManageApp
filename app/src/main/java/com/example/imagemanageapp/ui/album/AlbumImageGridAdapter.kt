package com.example.imagemanageapp.ui.album

import android.annotation.SuppressLint
import android.app.FragmentManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.SingleImageFragment
import kotlinx.android.synthetic.main.data.view.*


class AlbumImageGridAdapter : BaseAdapter {
    private val ctx: Context?
    private val transaction: FragmentTransaction?
    private val krTag : String
    private val enTag : String
    private val data: ArrayList<AlbumImage>

    constructor(_ctx: Context?, _transaction: FragmentTransaction?, _krTag : String, _enTag : String, _data: ArrayList<AlbumImage>) {
        ctx = _ctx
        transaction = _transaction
        krTag = _krTag
        enTag = _enTag
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

        val imageView = mView.findViewById<ImageView>(R.id.img)


        val token = data[position].token

        // 이미지 띄워주기
        Glide.with(ctx)
            .load(token).thumbnail(0.1f)
            .centerCrop()
            .into(imageView)


        val fragment = SingleImageFragment()
        val bundle = Bundle(1)
        bundle.putString("token", token)
        fragment.arguments = bundle
        Log.d("bundle", bundle.toString())


        // 클릭 시 사진 확대
        mView.setOnClickListener {
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        return mView
    }


}