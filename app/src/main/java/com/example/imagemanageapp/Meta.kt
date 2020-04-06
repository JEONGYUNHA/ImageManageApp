package com.example.imagemanageapp

import android.net.Uri

data class Meta(
    var id: String? = null,
    var title: String? = null,
    var path: String? = null,
    var date: Long? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var token: String? = null
)
