package com.example.imagemanageapp


data class Remove(
    var id: String? = null,
    var title: String? = null,
    var similar: Boolean? = null,
    var shaken: Boolean? = null,
    var darked: Boolean? = null,
    var unbalanced: Boolean? = null,
    var screenshot: Boolean? = null
)