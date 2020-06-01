package com.example.imagemanageapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList


class RecyclerView_Adapter(private var recyclerViewList: ArrayList<String>, private var recyclerIconViewList: ArrayList<Int>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var searchFilterList = ArrayList<String>()
    var searchIconFilterList = ArrayList<Int>()

    lateinit var mcontext: Context

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    init {
        searchFilterList = recyclerViewList
        searchIconFilterList = recyclerIconViewList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val searchImageListView =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)
        val sch = SearchHolder(searchImageListView)

        mcontext = parent.context

        return sch
    }

    override fun getItemCount(): Int {
        return searchFilterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.select_search_image_container.setBackgroundColor(Color.TRANSPARENT)

        holder.itemView.select_search_text.setTextColor(Color.BLACK)
        holder.itemView.select_search_text.text = searchFilterList[position]
        holder.itemView.recyclerview_icon.setImageResource(searchIconFilterList[position])

        holder.itemView.setOnClickListener {
            val intent = Intent(mcontext, SearchImageActivity::class.java)
            intent.putExtra("passselectedcountry", searchFilterList[position])
            mcontext.startActivity(intent)
            Log.d("Selected:", searchFilterList[position])
        }
    }

    //performFiltering 방법을 확인 우리는에 텍스트를 입력 한 경우 SeachView을 .
    //텍스트가 없으면 모든 항목을 반환합니다.
    //텍스트가 있으면 문자가 목록의 항목과 일치하는지 확인하고 결과를 FilterResults 유형으로 반환합니다 .
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    searchFilterList = recyclerViewList
                } else {   // 텍스트가 목록의 항목과 일치하는지 확인
                    val resultList = ArrayList<String>()
                    for (row in recyclerViewList) {
                        if (row.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    searchFilterList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = searchFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                searchFilterList = results?.values as ArrayList<String>
                searchIconFilterList = results?.values as ArrayList<Int>
                notifyDataSetChanged()
            }

        }
    }

}