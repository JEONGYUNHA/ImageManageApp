package com.example.imagemanageapp.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumViewModel : ViewModel(){
    private val _text = MutableLiveData<String>().apply {
        value = "This is album Fragment"
    }
    val text: LiveData<String> = _text
}