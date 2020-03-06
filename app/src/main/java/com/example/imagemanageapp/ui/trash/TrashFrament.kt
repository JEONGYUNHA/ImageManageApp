package com.example.imagemanageapp.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.imagemanageapp.R

class TrashFrament : Fragment() {

    private lateinit var trashViewModel: TrashViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trashViewModel =
            ViewModelProviders.of(this).get(TrashViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_trash, container, false)
        val textView: TextView = root.findViewById(R.id.text_trash)
        trashViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}