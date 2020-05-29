package com.example.imagemanageapp


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_search.*

data class SearchThings(val title: String)

class SearchActivity : AppCompatActivity() {

    lateinit var adapter: RecyclerView_Adapter
    lateinit var imageSearchrv: RecyclerView

    lateinit var okBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // 액션바 숨기기
        supportActionBar!!.hide()
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // SearchView 보여주기
        val searchIcon = image_search.findViewById<ImageView>(R.id.search_mag_icon)
        searchIcon.setColorFilter(Color.BLACK)


        val cancelIcon = image_search.findViewById<ImageView>(R.id.search_close_btn)
        cancelIcon.setColorFilter(Color.BLACK)

        val textView = image_search.findViewById<TextView>(R.id.search_src_text)
        textView.setTextColor(Color.BLACK)
        // If you want to change the color of the cursor, change the 'colorAccent' in colors.xml

        //--------------------------------------
        okBtn = findViewById(R.id.okButton)
        //--------------------------------------

        imageSearchrv = findViewById(R.id.image_rv)
        imageSearchrv.layoutManager = LinearLayoutManager(imageSearchrv.context)
        imageSearchrv.setHasFixedSize(true)

        image_search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        //--------------------------------------
        okBtn.setOnClickListener{
            var query = image_search.query.toString()
            val intent = Intent(this, SearchImageActivity::class.java)
            intent.putExtra("passselectedcountry", query)
            startActivity(intent)
        }
        //--------------------------------------

        getListOfCountries()
    }

    private fun getListOfCountries() {
        val searchImageRecyclerViewList = ArrayList<String>()

        searchImageRecyclerViewList.add("서울특별시")
        searchImageRecyclerViewList.add("어제")
        searchImageRecyclerViewList.add("최근 3개월")
        searchImageRecyclerViewList.add("최근 6개월")
        searchImageRecyclerViewList.add("작년 사진")
        searchImageRecyclerViewList.add("어두운 사진")
        searchImageRecyclerViewList.add("흔들린 사진")
        searchImageRecyclerViewList.add("유사한 사진")
        searchImageRecyclerViewList.add("스크린샷")

        adapter = RecyclerView_Adapter(searchImageRecyclerViewList)
        imageSearchrv.adapter = adapter
    }


}
