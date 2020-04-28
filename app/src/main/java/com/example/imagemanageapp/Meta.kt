package com.example.imagemanageapp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Meta(var id : String, var title : String, var path : String, var date : Long, var latitude : Double,
                var longitude : Double, var token : String, var deleted : Boolean) : Parcelable {

}
