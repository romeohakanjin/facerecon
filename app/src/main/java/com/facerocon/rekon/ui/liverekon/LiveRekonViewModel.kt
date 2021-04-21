package com.facerocon.rekon.ui.liverekon

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LiveRekonViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Live Rekon"
    }
    val text: LiveData<String> = _text
}