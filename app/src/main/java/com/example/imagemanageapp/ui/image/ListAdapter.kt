package com.example.imagemanageapp.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import com.example.imagemanageapp.R


class ListAdapter : BaseAdapter {
    private val ctx: Context?
    private val transaction: FragmentTransaction?
    private val data: ArrayList<Image>
    private val date: ArrayList<SimpleDate>

    constructor(_ctx: Context?, _transaction: FragmentTransaction?, _data: ArrayList<Image>, _date: ArrayList<SimpleDate>) {
        ctx = _ctx
        transaction = _transaction
        data = _data
        date = _date
    }

    override fun getCount(): Int {
        return date.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var mView = convertView
        val inflater = LayoutInflater.from(ctx)
        mView = inflater.inflate(R.layout.image_row, parent, false)

        val mGrid: GridView = mView.findViewById(R.id.gridView)
        val mText: TextView = mView.findViewById(R.id.textView)

        // 현재 리스트뷰의 날짜
        var currentDate = date[position].date
        val year = currentDate!!.split(".")[0]
        val month = currentDate!!.split(".")[1].toInt()
        // 한 자리 수 달(1~9)은 앞에 0 붙여서 출력(01~09)
        if(month < 10) {
            mText.text = String.format("%s.0%s", year, month)
        } else {
            mText.text = currentDate
        }


        // 현재 리스트뷰의 날짜에 해당하는 사진만 전달
        var currentData = arrayListOf<Image>()
        var i = 0
        for(i in 0 until data.size) {
            if(checkDate(data[i], currentDate))
                currentData.add(data[i])
        }

        val mAdapter = GridAdapter(ctx, transaction, currentData)
        mGrid.adapter = mAdapter




        /*// 각 메뉴별 클릭 시 이벤트 달기
        mGrid.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(ctx, data[position].toString(), Toast.LENGTH_SHORT).show()
        }*/


        // **** ListView 높이 조절 ****
        var totalHeight = 0
        // 현재 item의 갯수/2
        var currentSize = 0
        // 짝수
        if(currentData.size % 2 == 0)
            currentSize = currentData.size / 2
        // 홀수
        else
            currentSize = (currentData.size / 2) + 1

        // GridView 높이
        mGrid.measure(0, 0)
        var gridH = mGrid.measuredHeight
        // TextView 높이
        mText.measure(0, 0)
        var textH = mText.measuredHeight

        for(i in 0 until currentSize) {
            mView.measure(0,0)
            totalHeight += gridH
        }
        totalHeight += textH

        // Layout의 height를 변경하여 재설정해줌
        val params = mView.layoutParams
        params.height = totalHeight
        mView.layoutParams = params
        mView.requestLayout()


        return mView
    }

    // 주어진 년/월과 이미지의 년/월이 같은지 확인
    private fun checkDate(img : Image, date: String?) : Boolean{
        if(img.simpleDate.equals(date))
            return true
        return false
    }


}
