package com.dongkun.seniorgarten

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dongkun.seniorgarten.databinding.ActivityEmergencyCallBinding
import com.kakao.vectormap.LatLng

class EmergencyCallActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmergencyCallBinding
    private lateinit var centerPosition: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmergencyCallBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // KakaoMapViewModel의 LiveData를 관찰하여 UI 업데이트
        val viewModel: KakaoMapViewModel =
            ViewModelProvider(this).get(KakaoMapViewModel::class.java)
        Log.d("EmergencyCallActivity", viewModel.selectedPosition.hasActiveObservers().toString())
        viewModel.selectedPosition.observe(this) { latLng ->
            Log.d("EmergencyCallActivity", "Observed value: $latLng")
            if (latLng != null) {
                centerPosition = LatLng.from(latLng.latitude, latLng.longitude)
                val apiClient = ApiClient()
                apiClient.getAddressFromCoordinates(latLng.latitude, latLng.longitude) { address ->
                    if (address != null) {
                        Log.d("EmergencyCallActivity", "주소 찾음: $address")
                        runOnUiThread {
                            // UI 업데이트 등의 작업을 수행할 수 있음
                            binding.textView7.text = address
                        }
                    } else {
                        Log.e("EmergencyCallActivity", "주소를 찾을 수 없습니다.")
                    }
                }
            } else {
                Log.d("EmergencyCallActivity", "Observed value is null")
            }
        }
    }
}
