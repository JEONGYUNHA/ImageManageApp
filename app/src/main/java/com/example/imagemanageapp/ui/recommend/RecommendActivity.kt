package com.example.imagemanageapp.ui.recommend

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.GridView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.example.imagemanageapp.R
import com.example.imagemanageapp.SearchActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_recommand.*


class RecommendActivity  : AppCompatActivity() {
    // var tabLayout: TabLayout? = null

    val categoryDBList1 = arrayListOf<String>(
        "similar", "shaken", "darked", "screenshot"
    )
    private val list by lazy { findViewById<GridView>(R.id.list) }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var model1: MyViewModel1
    private lateinit var model2: MyViewModel2
    private lateinit var model3: MyViewModel3
    private lateinit var model4: MyViewModel4




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

        model1= ViewModelProvider(this)[MyViewModel1::class.java]
        model2= ViewModelProvider(this)[MyViewModel2::class.java]
        model3= ViewModelProvider(this)[MyViewModel3::class.java]
        model4= ViewModelProvider(this)[MyViewModel4::class.java]


        // Adapter에 Fragment 추가하기
        adapter.addFragment(RecommendFristFragment(), "유사사진")
        adapter.addFragment(RecommendSecondFragment(), "흔들린사진")
        adapter.addFragment(RecommendThirdFragment(), "어두운사진")
        adapter.addFragment(RecommendForthFragment(), "스크린샷")
        viewPager.adapter = adapter
        tabss.setupWithViewPager(viewPager)




        viewPager.addOnPageChangeListener( TabLayout.TabLayoutOnPageChangeListener(tabss))
        tabss.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            //탭이 선택되면
            override fun onTabSelected(tab: TabLayout.Tab) {
                var i = tab.position
                viewPager.currentItem = i
                if (tab.isSelected) {
                  // Log.d("tabb",i.toString())
                    deleteBtn1.setOnClickListener {
                        setParams(deleteBtn1,adapter.getItem(i),i)

                    }

                }

            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

                var i = tab.position
                viewPager.currentItem = i
                refreash(adapter.getItem(i),supportFragmentManager.beginTransaction())



            }
        })
        tabss.setupWithViewPager(viewPager)



    }

    fun refreash(fragment: Fragment,transaction: FragmentTransaction){
        //val transaction = supportFragmentManager.beginTransaction()
      //  transaction?.replace(R.id.nav_host_fragment,fragment)
    //    transaction.addToBackStack(null)
    //    transaction.commit()

     //   val ft: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.detach(fragment).attach(fragment).commit()
    }


    //삭제버튼 눌렀는지 확인하고 num값 보내기
    fun setParams(btn:Button,fragment: Fragment,i:Int){

    if(num == 0){ //deleteBtn이 처음눌렸을때
            Toast.makeText(this, "삭제할 사진을 선택하세요", Toast.LENGTH_SHORT).show()
            val iParam =
                btn.getLayoutParams() as RelativeLayout.LayoutParams
            btn.setBackgroundResource(R.drawable.checkbtn)
            btn.setLayoutParams(iParam)
            num++
            model1.numsPlus()
            model2.numsPlus()
            model3.numsPlus()
            model4.numsPlus()
        }else{//체크버튼 눌렸을때 (num == 1)
            val iParam =
                btn.getLayoutParams() as RelativeLayout.LayoutParams
            btn.setBackgroundResource(R.drawable.deletebtn)
            btn.setLayoutParams(iParam)
            num--
            model1.numsMinus()
            model2.numsMinus()
            model3.numsMinus()
            model4.numsMinus()
            for(ii in 0..3){
                if(i == 0){
                    model1.checkPlus()

                }else if(i == 1){
                    model2.checkPlus()
                }else if(i == 2){
                    model3.checkPlus()
                }else if(i == 3){
                    model4.checkPlus()

                }


            }

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
        menuInflater.inflate(R.menu.main, menu)
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