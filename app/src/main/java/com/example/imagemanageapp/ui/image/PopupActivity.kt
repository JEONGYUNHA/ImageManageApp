package com.example.imagemanageapp.ui.image

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.popup_metadata.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PopupActivity : AppCompatActivity() {
    private var db: FirebaseFirestore? = null
    private var meta : Meta? = null
    private var docTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_metadata)
        db = FirebaseFirestore.getInstance()

        // 메타 정보 넘겨받음
        meta = intent.getParcelableExtra<Meta>("datas")
        docTitle = String.format("%s-%s", meta!!.id, meta!!.title)
    }

    override fun onStart() {
        super.onStart()

        // 색 3가지 띄우기
        showColors()

        // Tag 띄우기
        showTags()

        // 위경도로 주소 불러오기
        var location = ""
        if (meta!!.latitude != 0.0 && meta!!.longitude != 0.0) {
            try {
                val mGeocoder = Geocoder(baseContext)
                location =
                    mGeocoder.getFromLocation(meta!!.latitude, meta!!.longitude, 1)[0].getAddressLine(0)
                Log.d("location", location)
            } catch (e: IOException) {
                e.printStackTrace()
                Log.d("location", "failed")
            }
        }

        // 날짜 Long타입에서 Date타입으로 바꾸기
        var date = DateToString(Date(meta!!.date))

        titleField.text = meta!!.title
        pathField.text = meta!!.path
        dateField.text = date
        locationField.text = location


        closeBtn.setOnClickListener() {
            finish()
        }


    }

    // Date를 String으로 바꿔주는 함수
    private fun DateToString(date: Date): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
        val str = dateFormat.format(date)

        return str
    }

    // 저장된 3개의 색을 불러와 view 3개에 띄워줌
    private fun showColors() {
        var c = arrayListOf<Int>()
        db!!.collection("color").document(docTitle).collection("colors").get().addOnSuccessListener {documents->
            for(d in documents) {
                c.add(d.get("intColor").toString().toInt())
            }
            color1.setBackgroundColor(c[0])
            color2.setBackgroundColor(c[1])
            color3.setBackgroundColor(c[2])

        }

    }

    // Tag 띄우기
    private fun showTags() {
        db!!.collection("remove").document(docTitle).get().addOnSuccessListener {
            if(it.get("shaken") as Boolean) { tagField.append("#흔들린 ") }
            if(it.get("darked") as Boolean) { tagField.append("#어두운 ") }
            if(it.get("screenshot") as Boolean) { tagField.append("#스크린샷 ") }
            if(it.get("similar") as Boolean) { tagField.append("#유사한 ") }
        }
        db!!.collection("auto").document(docTitle).get().addOnSuccessListener {
            if(it.get("person") as Boolean) { tagField.append("#사람 ") }
            if(it.get("animal") as Boolean) { tagField.append("#동물 ") }
            if(it.get("traffic") as Boolean) { tagField.append("#교통수단 ") }
            if(it.get("furniture") as Boolean) { tagField.append("#가구 ") }
            if(it.get("book") as Boolean) { tagField.append("#책 ") }
            if(it.get("bag") as Boolean) { tagField.append("#가방 ") }
            if(it.get("sport") as Boolean) { tagField.append("#스포츠 ") }
            if(it.get("device") as Boolean) { tagField.append("#전자기기 ") }
            if(it.get("plant") as Boolean) { tagField.append("#식물 ") }
            if(it.get("food") as Boolean) { tagField.append("#음식 ") }
            if(it.get("things") as Boolean) { tagField.append("#잡동사니 ") }
        }
    }
}
