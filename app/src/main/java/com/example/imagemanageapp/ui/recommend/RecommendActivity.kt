package com.example.imagemanageapp.ui.recommend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.ui.AppBarConfiguration
import com.example.imagemanageapp.R
import com.example.imagemanageapp.R.id.tabss
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


    val db = FirebaseFirestore.getInstance()
    var tList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommand)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //뒤로가기버튼 추가
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


       val tabLayout: TabLayout = findViewById(R.id.tabss)


        val adapter = MyPagerAdapter(supportFragmentManager)
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
                tabSelect()



            }
            override fun onTabUnselected(tab: TabLayout.Tab) {

            }
            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        tabss.setupWithViewPager(viewPager)
    }

    fun tabSelect() {
        for (i in 0..3) {
            val tab = tabss.getTabAt(i)!!
            if (tab.isSelected) {
                deleteBtn.setOnClickListener {
                    var button = Button(this)
                  //  button.background =
                }
            }
            // TabBtnText.get(i).setTextColor(resources.getColor(R.color.point))
            // } else {
            // tab.setIcon(tabBtnImgOff[i])
            // TabBtnText.get(i).setTextColor(resources.getColor(R.color.lightGray)) }


        }
    }


    override fun onStart() {
        super.onStart()

        //actionbar 타이틀 없애주는 코드
        val actionBar = supportActionBar
        if(actionBar != null) actionBar.setDisplayShowTitleEnabled(false)


        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
       // val navView: NavigationView = findViewById(R.id.nav_view)
      //  val navController = findNavController(R.id.nav_host_fragment)
      /*  appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_image, R.id.nav_album, R.id.nav_recommend,
                R.id.nav_mypage, R.id.nav_trash
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        // HeaderView 접근하여 프로필 변경
        val headerView = navView.getHeaderView(0)
        val pref = this.getSharedPreferences("id", Context.MODE_PRIVATE)
        headerView.idField.text = pref.getString("id", "User")
        headerView.emailField.text = pref.getString("email", "Email")

*/
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


 /*   // 왼쪽 카테고리 메뉴
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/

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