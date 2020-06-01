package com.example.imagemanageapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_details.*
import java.util.*
import kotlin.collections.ArrayList

data class SearchImageCategory (
    var token: String? = null,
    var date : Long = 0
)
class SearchImageActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private var tokens = ArrayList<SearchImageCategory>()
    private var search_front_time : Long = 0
    private var search_after_time : Long = 0
    private var searchPlace : String = ""

    var dbCollection : String? = null
    var dbField : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_details)
    }

    override fun onResume() {
        super.onResume()

        tokens.clear()
        var nowTime: Calendar?

        var compare_front_time : Calendar
        var compare_after_time : Calendar
        val search_item = intent.extras!!.getString("passselectedcountry")!!

        var removeTableTrue = 0;
        var metaTableTrue = 0;
        var metaTablePlaceTrue = 0;

        // 액션바 지우기
        supportActionBar?.title = search_item
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (search_item.contains("흔들린")) {
            dbCollection = "remove"
            dbField = "shaken"
            removeTableTrue++
        } else if(search_item.contains("어두운")) {
            dbCollection = "remove"
            dbField = "darked"
            removeTableTrue++
        } else if(search_item.contains("유사")||search_item.contains("비슷")) {
            dbCollection = "remove"
            dbField = "similar"
            removeTableTrue++
        } else if(search_item.contains("스크린샷")) {
            dbCollection = "remove"
            dbField = "screenshot"
            removeTableTrue++
        }

        if(search_item=="사람"||search_item=="동물"||search_item=="가방"||search_item=="책"||search_item=="음식"||search_item=="가구"||search_item=="식물"||search_item=="스포츠"||search_item=="잡동사니"||search_item=="전자기기"||search_item.contains("전자")||search_item=="교통"){
            dbCollection = "auto"
            if(search_item=="사람")
                dbField = "person"
            if(search_item=="동물")
                dbField = "animal"
            if(search_item=="가방")
                dbField = "bag"
            if(search_item=="책")
                dbField = "book"
            if((search_item=="전자기기")||search_item.contains("전자"))
                dbField = "device"
            if(search_item=="음식")
                dbField = "food"
            if(search_item=="가구")
                dbField = "furniture"
            if(search_item=="식물")
                dbField = "plant"
            if(search_item=="스포츠")
                dbField = "sport"
            if(search_item=="잡동사니")
                dbField = "things"
            if(search_item=="교통")
                dbField = "traffic"

            removeTableTrue++
        }

        if(removeTableTrue>0){
            searchImageCategory(dbCollection, dbField)
        }

        //------------------날짜 비교
        if (search_item.contains("오늘")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            Log.d("check now Time 오늘", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 오늘",compare_front_time.toString())

            compare_after_time = Calendar.getInstance()
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 오늘",compare_after_time.toString())
            metaTableTrue++
        } else if (search_item.contains("어제")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.DATE, -1)
            Log.d("check now Time 어제", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 어제",compare_front_time.toString())

            compare_after_time =nowTime
            compare_after_time.set(Calendar.HOUR, 23)
            compare_after_time.set(Calendar.MINUTE, 59)
            compare_after_time.set(Calendar.SECOND, 59)
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 어제",compare_after_time.toString())
            metaTableTrue++
        } else if (search_item.contains("일주일")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.DATE, -7)
            Log.d("check now Time 일주일", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 일주일",compare_front_time.toString())

            compare_after_time = Calendar.getInstance()
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 일주일",compare_after_time.toString())
            metaTableTrue++
        } else if (search_item.contains("한달")||search_item.contains("한 달")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.MONTH, -1)
            Log.d("check now Time 한달", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 한달",compare_front_time.toString())

            compare_after_time = Calendar.getInstance()
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 한달",compare_after_time.toString())
            metaTableTrue++
        } else if (search_item.contains("6개월")||search_item.contains("6 개월")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.MONTH, -6)
            Log.d("check now Time 6개월", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 6개월",compare_front_time.toString())

            compare_after_time = Calendar.getInstance()
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 6개월",compare_after_time.toString())
            metaTableTrue++
        }else if (search_item.contains("3개월")||search_item.contains("3 개월")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.MONTH, -3)
            Log.d("check now Time 3개월", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time 3개월",compare_front_time.toString())

            compare_after_time = Calendar.getInstance()
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time
            Log.d("compare_after_time 3개월",compare_after_time.toString())
            metaTableTrue++
        } else if (search_item.contains("일년")||search_item.contains("일 년")||search_item.contains("1년")||search_item.contains("1 년")||search_item.contains("작년")) {

            nowTime = Calendar.getInstance()
            nowTime.time = Date()
            nowTime.add(Calendar.YEAR, -1)
            Log.d("check now Time 1년 전 사진", nowTime.toString())

            compare_front_time = nowTime
            compare_front_time.set(Calendar.MONTH, 0)
            compare_front_time.set(Calendar.DATE, 1)
            compare_front_time.set(Calendar.HOUR_OF_DAY, 0)
            compare_front_time.set(Calendar.MINUTE, 0)
            compare_front_time.set(Calendar.SECOND, 0)
            var calendarToDate : Date = compare_front_time.time
            search_front_time = calendarToDate.time
            Log.d("compare_front_time",compare_front_time.toString())

            compare_after_time = nowTime
            compare_after_time.set(Calendar.MONTH, 11)
            compare_after_time.set(Calendar.DATE, 31)
            compare_after_time.set(Calendar.HOUR_OF_DAY, 23)
            compare_after_time.set(Calendar.MINUTE, 59)
            compare_after_time.set(Calendar.SECOND, 59)
            var calendarToDate2 : Date = compare_after_time.time
            search_after_time = calendarToDate2.time

            Log.d("compare_after_time",compare_after_time.toString())
            metaTableTrue++
        }

        if(metaTableTrue>0){
            searchImageDate(search_front_time, search_after_time)
        }

        // -----------------------장소 검색

        if(search_item.contains("서울")||search_item.contains("성북")||search_item.contains("수유")||search_item.contains("경기")||search_item.contains("인천")||search_item.contains("파주")||search_item.contains("군포")){
            dbCollection = "meta"
            dbField = "place"
            Log.d("search_item 위치 확인", search_item)
            metaTablePlaceTrue++
        }

        if(metaTablePlaceTrue>0){
            searchImagePlace(search_item)
        }

    }

    // remove table 검색
    private fun searchImageCategory(collection: String?, field: String?) {
        tokens.clear()
        Log.d("check collection",collection)
        Log.d("check field",field)
        if (collection != null) {
            if (field != null) {
                db.collection(collection)
                    .whereEqualTo(field, true)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                            db.collection("meta").document(docTitle).get().addOnSuccessListener {
                                val token = it.get("token").toString()
                                var date = it.get("date").toString().toLong()
                                tokens.add(SearchImageCategory(token,date))
                                if(documents.size() == tokens.size)
                                    showImages()
                            }
                        }
                    }
            }
        }
    }

    // 날짜 검색
    private fun searchImageDate(search_front_time: Long, search_after_time: Long) {
        var token : String? = null
        var date : Long = 0

        // 시간 비교
        db.collection("meta")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    val dateLong = document.get("date").toString().toLong()
                    if ((search_front_time < dateLong) && (search_after_time > dateLong)) {
                        db.collection("meta").document(docTitle).get().addOnSuccessListener {
                            token = it.get("token").toString()
                            date = it.get("date").toString().toLong()
                            tokens.add(SearchImageCategory(token, date))
                        }
                    }
                }
            }
        db.collection("meta")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    val dateLong = document.get("date").toString().toLong()
                    if ((search_front_time < dateLong) && (search_after_time > dateLong)) {
                        db.collection("meta").document(docTitle).get().addOnSuccessListener {
                            showImages()
                        }
                    }
                }
            }
    }

    // 장소 검색
    private fun searchImagePlace(searchPlace: String) {
        var token : String? = null
        var date : Long = 0

        db.collection("meta")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    val docPlace = document.get("place").toString()
                    Log.d("document place 장소 확인", docPlace)
                    if (docPlace.contains(searchPlace)) {
                        db.collection("meta").document(docTitle).get().addOnSuccessListener {
                            token = it.get("token").toString()
                            date = it.get("date").toString().toLong()
                            tokens.add(SearchImageCategory(token, date))
                        }
                    }
                }
            }
        db.collection("meta")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docTitle = String.format("%s-%s", document.get("id").toString(), document.get("title").toString())
                    val docPlace = document.get("place").toString()
                    if (docPlace.contains(searchPlace)) {
                        db.collection("meta").document(docTitle).get().addOnSuccessListener {
                            showImages()
                        }
                    }
                }
            }
    }

    // GridView에 inflate 하기
    private fun showImages() {
        val mGrid = searchGridView
        tokens.sortByDescending { data: SearchImageCategory -> data.date }
        Log.d("check token data", tokens.toString())
        val mAdapter = SearchImageGridAdapter(this, tokens)
        mGrid.adapter = mAdapter

    }

}
