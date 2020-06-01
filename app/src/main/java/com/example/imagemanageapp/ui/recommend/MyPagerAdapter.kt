package com.example.imagemanageapp.ui.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModel

// Tab Layout 하는 Adapter
class MyPagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

    private val rFragmentList = ArrayList<Fragment>()
    private val rFragmentCategoryList = arrayListOf<String>()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> RecommendFristFragment()
            1 -> RecommendSecondFragment()
            2 -> RecommendThirdFragment()
            else -> {
                return RecommendForthFragment()
            }
        }
    }
    override fun getCount(): Int {
        return rFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return rFragmentCategoryList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        rFragmentList.add(fragment)
        rFragmentCategoryList.add(title)
    }

   /* fun refreash(fragment: Fragment){
        val tr = supportFragmentManager.beginTransaction()
        tr.detach(this).attach(this).commit()
    }  */

    fun getModel(num:Int):ViewModel{
        return when (num) {
            0 -> MyViewModel1()
            1 -> MyViewModel2()
            2 -> MyViewModel3()
            else -> {
                return MyViewModel4()
            }
        }

    }
}