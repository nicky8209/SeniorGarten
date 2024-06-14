package com.example.seniorgarten

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.seniorgarten.databinding.ActivityReservationBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import java.lang.Exception

class ReservationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationBinding

    private lateinit var label: Label
    private lateinit var tvMapViewLatLng: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReservationBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        tvMapViewLatLng = binding.tvMapViewLatlng
        val mapView: MapView = binding.mapView
        setupMapView(mapView)
    }

    private fun setupMapView(mapView: MapView) {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                TODO("Not yet implemented")
            }

            override fun onMapError(p0: Exception?) {
                TODO("Not yet implemented")
            }

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(map: KakaoMap) {
                val viewport = map.viewport
                val x = viewport.width() / 2
                val y = viewport.height() / 2

                val centerPosition = map.fromScreenPoint(x, y)
                if (centerPosition != null) {
                    tvMapViewLatLng.text =
                        "Lat = ${centerPosition.latitude}\nLng = ${centerPosition.longitude}"

                }

                val point = map.toScreenPoint(centerPosition)

                val labelLayer = map.labelManager?.layer
                if (labelLayer != null) {
                    // labelLayer가 null이 아닌 경우에만 addLabel() 메서드를 호출합니다.
                    if (point != null) {
                        label = labelLayer.addLabel(
                            LabelOptions.from(map.fromScreenPoint(point.x, point.y))
                                .setStyles(R.drawable.green_marker)
                        )
                    }
                }

                map.setOnCameraMoveEndListener { _, _, _ ->
                    val viewport = map.viewport
                    val x = viewport.width() / 2
                    val y = viewport.height() / 2

                    val position = map.fromScreenPoint(x, y)
                    label.moveTo(position)

                    if (position != null) {
                        tvMapViewLatLng.text =
                            "Lat = ${position.latitude}\nLng = ${position.longitude}"

                    }
                }
            }

        })

    }
}