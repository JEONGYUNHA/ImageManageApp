package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import java.text.FieldPosition

class SetCategoryFragment :Fragment(){


    private val db = FirebaseFirestore.getInstance()

    val categoryList = arrayListOf<String>(
        "유사사진", "흔들린사진", "어두운사진", "불균형사진", "스크린샷"
    )
    val categoryDBList = arrayListOf<String>(
        "similar","shaken","darked","unbalanced","screenshot"
    )

    /*  override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {

         super.onListItemClick(l, v, position, id)
          Log.d("ClickItem",position.toString())*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val root = inflater.inflate(R.layout.fragment_recommendlist, container, false)

        //listView 찾아서 adapter에 달아주기
        val listView = root.findViewById<ListView>(R.id.listview)
        val cAdapter = CategoryAdapter(this.activity,categoryList)

        listView.adapter = cAdapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->

            read(position)
        }
        return root
    }

    //position읽어서 해당 데이터 보내는 함수
    private fun read(position: Int) {
        val tList = ArrayList<String>()
        val categoryNum = categoryDBList[position]
        Log.d("category2", categoryNum)
        db.collection("remove")
            .whereEqualTo(categoryNum!!, true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("title").toString()
                    tList.add(name)
                }
                //데이터 보내기
                //position을 둔 필드에 해당하는 사진의 이름 리스트 보내기
                val newFragment = ShowCategoryImageFragment()
                val titleList = Bundle()
                titleList.putString("titleList",tList.toString())
                newFragment.arguments = titleList

                //fragment to fragment 전환
                val transaction = parentFragmentManager.beginTransaction()
                transaction?.replace(R.id.nav_host_fragment,newFragment)
                transaction.addToBackStack(null)
                transaction.commit()

                Log.d("titleList2",tList.toString())

            }
            .addOnFailureListener {
                Log.d("failUpload", "")
            }
    }
}
