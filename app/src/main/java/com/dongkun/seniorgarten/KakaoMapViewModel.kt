package com.dongkun.seniorgarten

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class KakaoMapViewModel : ViewModel() {
    fun setSelectedPosition(latitude: Double, longitude: Double) {
        _selectedPosition.value = Pair(latitude, longitude)
    }

    private val _selectedPosition = MutableLiveData<Pair<Double, Double>>().apply {
        value = Pair(36.5869233979831, 128.187405044754)

    }
    val selectedPosition: LiveData<Pair<Double, Double>> = _selectedPosition

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
}