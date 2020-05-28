package com.example.imagemanageapp.ui.album

import android.annotation.SuppressLint
import android.content.Context
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.imagemanageapp.R
import com.example.imagemanageapp.ui.image.Image
import com.example.imagemanageapp.ui.image.SingleImageFragment
import kotlinx.android.synthetic.main.data.view.*

class AlbumGridAdapter : BaseAdapter {
    private val ctx: Context?
    private val transaction: FragmentTransaction?
    private val data: ArrayList<Album>

    constructor(_ctx: Context?, _transaction: FragmentTransaction?, _data: ArrayList<Album>) {
        ctx = _ctx
        transaction = _transaction
        data = _data
        Log.d("adapter", "true")
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
        mView = inflater.inflate(R.layout.album_row, parent, false)

        val imageView = mView.findViewById<ImageView>(R.id.imageView)
        val albumTitle = mView.findViewById<TextView>(R.id.albumTitle)
        val albumSize = mView.findViewById<TextView>(R.id.albumSize)

        val rounded = ctx!!.getDrawable(R.drawable.roundedimageview)
        imageView.background = rounded
        imageView.clipToOutline = true

        val token = data[position].token
        // 이미지 띄워주기
        Glide.with(ctx)
            .load(token).thumbnail(0.1f)
            .centerCrop()
            .into(imageView)

        // 앨범 이름과 사진 수 띄우기
        albumTitle.text = data[position].koreanTag
        albumSize.text = data[position].size.toString()

        val fragment = AlbumImageFragment()
        val bundle = Bundle(2)
        bundle.putString("krTag", data[position].koreanTag)
        bundle.putString("enTag", data[position].englishTag)
        fragment.arguments = bundle

        val linear = mView.findViewById<LinearLayout>(R.id.linearLayout)
        linear.setOnClickListener {
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }

        /*mView!!.findViewById<Button>(R.id.moreBtn).setOnClickListener {
            transaction?.replace(R.id.nav_host_fragment, fragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }*/

        return mView
    }


}