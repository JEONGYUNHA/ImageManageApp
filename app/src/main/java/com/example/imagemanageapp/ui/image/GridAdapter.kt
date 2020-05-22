package com.example.imagemanageapp.ui.image

import android.annotation.SuppressLint
import android.app.FragmentManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R
import kotlinx.android.synthetic.main.data.view.*


class GridAdapter : BaseAdapter {
    private val ctx: Context?
    private val transaction: FragmentTransaction?
    private val data: ArrayList<Image>

    constructor(_ctx: Context?, _transaction: FragmentTransaction?, _data: ArrayList<Image>) {
        ctx = _ctx
        transaction = _transaction
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

        val imageView = mView.img

        val d = data[position]

        // 이미지 띄워주기
        Glide.with(ctx)
            .load(d.token).thumbnail(0.1f)
            .centerCrop()
            .into(imageView)


        val fragment = SingleImageFragment()
        val bundle = Bundle(1)
        bundle.putString("token", d.token)
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