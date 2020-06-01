package com.example.imagemanageapp.ui.recommend

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MyViewModel2 : ViewModel(){

    private var nums : Int = 0
    val checkDone: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    fun numsPlus(){
       // nums.plus(1)
        nums ++
    }
    fun numsMinus(){
     //  nums.minus(1)
        nums --
    }

    fun getNums():Int{
        return nums
    }

    fun checkMinus() {
        checkDone.setValue(0)
    }

    fun checkPlus() {
        checkDone.setValue(1)
    }



}