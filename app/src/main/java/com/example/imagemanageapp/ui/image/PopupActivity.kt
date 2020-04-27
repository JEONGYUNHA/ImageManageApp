package com.example.imagemanageapp.ui.image

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R
import kotlinx.android.synthetic.main.popup_metadata.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PopupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_metadata)
    }

    override fun onStart() {
        super.onStart()

        // 메타 정보 넘겨받음
        val meta = intent.getParcelableExtra<Meta>("datas")

        // 위경도로 주소 불러오기
        var location = ""
        if(meta.latitude != 0.0 && meta.longitude != 0.0) {
            try{
                val mGeocoder = Geocoder(baseContext)
                location = mGeocoder.getFromLocation(meta.latitude, meta.longitude, 1)[0].getAddressLine(0)
                Log.d("location", location)
            } catch (e : IOException) {
                e.printStackTrace()
                Log.d("location", "failed")
            }
        }

        // 날짜 Long타입에서 Date타입으로 바꾸기
        var date = DateToString(Date(meta.date))

        titleField.text = meta.title
        pathField.text = meta.path
        dateField.text = date
        locationField.text = location
        tagField.text = "null"

        closeBtn.setOnClickListener {
            finish()
        }


    }

    // Date를 String으로 바꿔주는 함수
    private fun DateToString(date: Date): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
        val str = dateFormat.format(date)

        return str
    }
}