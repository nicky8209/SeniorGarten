package com.dongkun.seniorgarten

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kakao.vectormap.LatLng

class KakaoMapViewModel : ViewModel() {
    fun setSelectedPosition(latitude: Double, longitude: Double) {
        _selectedPosition.value = LatLng.from(latitude, longitude)
    }

    private val _selectedPosition = MutableLiveData<LatLng>().apply {}
    val selectedPosition: LiveData<LatLng> = _selectedPosition

    private val _text = MutableLiveData<String>().apply { value = "This is dashboard Fragment" }
    val text: LiveData<String> = _text
}
