package com.example.imagemanageapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_search_main.*

data class SearchThings(val title: String, val image: Int)

class MainFragment : Fragment() {

    private val searchList = listOf(
        SearchThings("최근 검색 1",  R.drawable.map_pin),
        SearchThings("최근 검색 2",  R.drawable.map_pin),
        SearchThings("최근 검색 3",  R.drawable.map_pin),
        SearchThings("서울특별시",  R.drawable.seoul),
        SearchThings("최근 6개월 검색",  R.drawable.six_month),
        SearchThings("어두운 사진",  R.drawable.dark),
        SearchThings("흔들린 사진",  R.drawable.shaken),
        SearchThings("유사한 사진",  R.drawable.similar)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_search_main, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = SearchListAdapter(searchList)
        }
    }

    companion object {
        fun newInstance(): MainFragment = MainFragment()
    }
}