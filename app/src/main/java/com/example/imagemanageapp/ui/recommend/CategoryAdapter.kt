package com.example.imagemanageapp.ui.recommend

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.imagemanageapp.R


class CategoryAdapter : BaseAdapter{
    private val ctx: Context?
    private val data: ArrayList<String>




    constructor(_ctx:Context?,_data:ArrayList<String>){
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val inflater = LayoutInflater.from(ctx)
        view = inflater.inflate(R.layout.category_item,parent,false)

        val name = view.findViewById<TextView>(R.id.categoryItem)
        name.text = data.get(position)

        return view


    }


}
