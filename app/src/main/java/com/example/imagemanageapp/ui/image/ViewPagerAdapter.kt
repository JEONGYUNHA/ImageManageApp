package com.example.imagemanageapp.ui.image

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.imagemanageapp.Meta
import com.example.imagemanageapp.R

class ViewPagerAdapter(private var context: Context, private var images: ArrayList<Meta>) : PagerAdapter() {
    private var inflater : LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflater!!.inflate(R.layout.fragment_singleimage, null)
        /*val imageView = v.findViewById(R.id.imageView) as ImageView

        Glide.with(this.context)
            .load(images[position].token)
            .into(imageView)

        val vp = container as ViewPager
        vp.addView(v , 0)

*/
        return v

    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val v = `object` as View
        vp.removeView(v)
    }
}