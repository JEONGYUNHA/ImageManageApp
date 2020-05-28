package com.example.imagemanageapp.ui.recommend

import android.R.attr.left
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.ui.AppBarConfiguration
import com.example.imagemanageapp.R
import com.example.imagemanageapp.SearchActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_recommand.*
import kotlinx.android.synthetic.main.fragment_recommend_secondfragment.*
import kotlinx.android.synthetic.main.fragment_showcategory_image.view.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.*
import kotlinx.android.synthetic.main.fragment_showcategory_image_list.grid


class RecommendActivity  : AppCompatActivity() {
    // var tabLayout: TabLayout? = null

    val categoryDBList1 = arrayListOf<String>(
        "similar", "shaken", "darked", "screenshot"
    )
    private val list by lazy { findViewById<GridView>(R.id.list) }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var model: MyViewModel


    val db = FirebaseFirestore.getInstance()
    var tList = ArrayList<String>()
    var num = 0
    val adapter = MyPagerAdapter(supportFragmentManager)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = R.layout.activity_recommand
        setContentView(view)



        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //상단에 뒤로가기버튼 추가
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)




   /*     for(i in 0..3) {
            val newFragment = adapter.getItem(i)
            val num = Bundle()
            num.putString("num", num.toString())
            newFragment.arguments = num

            //fragment to fragment 전환
            val transaction = supportFragmentManager.beginTransaction()
            transaction?.replace(R.id.nav_host_fragment, newFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
*/

        val newFragment = RecommendSecondFragment()
        val num1 = Bundle()
        num1.putInt("num",num)
        newFragment.arguments = num1

        //fragment to fragment 전환
        val transaction = supportFragmentManager.beginTransaction()
        transaction?.replace(R.id.nav_host_fragment,newFragment)
        transaction.addToBackStack(null)
        transaction.commit()



        // Adapter에 Fragment 추가하기
        adapter.addFragment(RecommendFristFragment(), "유사사진")
        adapter.addFragment(RecommendSecondFragment(), "흔들린사진")
        adapter.addFragment(RecommendThirdFragment(), "어두운사진")
        adapter.addFragment(RecommendForthFragment(), "스크린샷")
        viewPager.adapter = adapter
        tabss.setupWithViewPager(viewPager)




        viewPager.addOnPageChangeListener( TabLayout.TabLayoutOnPageChangeListener(tabss))
        tabss.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                var i = tab.position
                viewPager.currentItem = i
                if (tab.isSelected) {
                    deleteBtn1.setOnClickListener {
                        setParams(deleteBtn1,adapter.getItem(i))

                    }

                }


             //   tabSelect()

            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        tabss.setupWithViewPager(viewPager)



    }
/*
    fun tabSelect() {
        for (i in 0..3) {
            val tab = tabss.getTabAt(i)!!
            if (tab.isSelected) {
                deleteBtn1.setOnClickListener {
                    setParams(deleteBtn1,adapter.getItem(i))



                }
            }
        }
    }
*/
    //삭제버튼 눌렀는지 확인하고 num값 보내기
    fun setParams(btn:Button,fragment: Fragment){
    //var model : MyViewModel
    model= ViewModelProvider(this)[MyViewModel::class.java]
   // model = ViewModelProviders.of(this).get(MyViewModel::class.java)
    if(num == 0){ //deleteBtn이 처음눌렸을때
            Toast.makeText(this, "삭제할 사진을 선택하세요", Toast.LENGTH_SHORT).show()
            val iParam =
                btn.getLayoutParams() as RelativeLayout.LayoutParams
            btn.setBackgroundResource(R.drawable.checkbtn)
            btn.setLayoutParams(iParam)
            num++
            model.numsPlus()
        }else{//체크버튼 눌렸을때 (num == 1)
            val iParam =
                btn.getLayoutParams() as RelativeLayout.LayoutParams
            btn.setBackgroundResource(R.drawable.deletebtn)
            btn.setLayoutParams(iParam)
            num--
            model.numsMinus()
            //model.checkPlus()
            model.checkPlus()

        }

    }


    override fun onStart() {
        super.onStart()

        //actionbar 타이틀 없애주는 코드
        val actionBar = supportActionBar
        if(actionBar != null) actionBar.setDisplayShowTitleEnabled(false)




    }

    // 상단 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_app, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.getItemId()
        // 상단 메뉴 중 검색 버튼 선택 시
        if (id == R.id.action_search) {
            val searchIntent = Intent(this, SearchActivity::class.java)
            startActivity(searchIntent)
        }else if(id == android.R.id.home){ // 뒤로가기 버튼 선택 시
            onBackPressed()
            //  val backIntent = Intent(this, MainActivity::class.java)
            //  startActivity(backIntent)
        }
        return super.onOptionsItemSelected(item)


    }


    private fun getTitleList(pos:Int,fragment:Fragment):String{

        //position읽어서 해당 데이터 tList로 만들기
        val categoryNum = categoryDBList1[pos]
        db.collection("remove")
            .whereEqualTo(categoryNum!!, true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.get("title").toString()
                    tList.add(name)
                    Log.d("!!!List", tList.toString())
                }

                Log.d("ttList", tList.toString())
                //데이터 보내기
                //position을 둔 필드에 해당하는 사진의 이름 리스트 보내기
                val newFragment = fragment
                val titleList = Bundle()
                titleList.putString("titleList",tList.toString())
                newFragment.arguments = titleList

                //fragment to fragment 전환
                val transaction = supportFragmentManager.beginTransaction()
                transaction?.replace(R.id.nav_host_fragment,newFragment)
                transaction.addToBackStack(null)
                transaction.commit()



            }
        Log.d("ssList", tList.toString())
        return tList.toString()

    }
}