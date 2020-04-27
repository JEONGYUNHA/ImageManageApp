package com.example.imagemanageapp.ui.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_recommend.*
import kotlinx.android.synthetic.main.fragment_recommend.view.*

class RecommendFrament : Fragment() {

    private lateinit var recommendViewModel: RecommendViewModel
    private val db = FirebaseFirestore.getInstance()
    var categoryList = ArrayList<String>()



    //fragment 시작하면 불리는 함수
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // recommendViewModel =
       //    ViewModelProviders.of(this).get(RecommendViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_recommend, container, false)!!
        val textView: TextView = root.findViewById(R.id.text_recommend)
        //카테고리순 버튼 누르면 카테고리순 엑티비티로 이동
        val setCategoty: Button = root.findViewById((R.id.setCategory))
        val transaction = parentFragmentManager.beginTransaction()


        root.setCategory.setOnClickListener {
            transaction?.replace(R.id.nav_host_fragment,SetCategoryFragment())
            back(transaction)
        }

        //날짜순 버튼 누르면 날짜순 엑티비티로 이동
        val setDate: Button = root.findViewById((R.id.setDate))
        root.setOnClickListener {


        }


        return root
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
    }


    //뒤로가기 누르면 이전 fragment로 가는 함수
    fun back(t:FragmentTransaction){
        t?.addToBackStack(null)
        t?.commit()
    }









}