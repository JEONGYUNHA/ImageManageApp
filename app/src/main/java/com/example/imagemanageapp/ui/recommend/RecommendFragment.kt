package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.imagemanageapp.R

class RecommendFrament : Fragment() {

    private lateinit var recommendViewModel: RecommendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        recommendViewModel =
            ViewModelProviders.of(this).get(RecommendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recommend, container, false)
        val textView: TextView = root.findViewById(R.id.text_recommend)
        recommendViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}