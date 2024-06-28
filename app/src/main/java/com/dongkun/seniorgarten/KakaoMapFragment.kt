package com.dongkun.seniorgarten

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dongkun.seniorgarten.databinding.FragmentKakaoMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions

class KakaoMapFragment : Fragment() {

    private var _binding: FragmentKakaoMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var label: Label
    private lateinit var tvMapViewLatLng: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val kakaoMapViewModel =
            ViewModelProvider(requireActivity()).get(KakaoMapViewModel::class.java)

        _binding = FragmentKakaoMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tvMapViewLatLng = binding.tvMapViewLatlng
        val mapView: MapView = binding.mapView
        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                        setupMapView(mapView, kakaoMapViewModel)
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                    }
                    else -> {
                        // No location access granted.
                    }
                }
            }

        // ...

        // Before you perform the actual permission request, check whether your app
        // already has the permissions, and whether your app needs to show a permission
        // rationale dialog. For more details, see Request permissions.
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        return root
    }

    private fun setupMapView(mapView: MapView, kakaoMapViewModel: KakaoMapViewModel) {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(e: Exception) {}
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(
                    map: KakaoMap
                ) { //                setupReservationButton(p0)
                    //                    setupLocation(p0)
                    updateMapPosition(map, kakaoMapViewModel, "centerPosition")

                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        map.setOnCameraMoveEndListener { _, _, _ ->
                            updateMapPosition(map, kakaoMapViewModel, "position")
                        }
                    }
                    setupLocation(map)
                }
            }
        )
    }

    private fun updateMapPosition(map: KakaoMap, kakaoMapViewModel: KakaoMapViewModel, b: String) {
        val viewport: Rect = map.viewport

        val x = viewport.width() / 2
        val y = viewport.height() / 2

        Log.d("KakaoMapFragment", "updateMapPostition, b = $b")
        when (b) {
            "centerPosition" -> {
                val centerPosition = map.fromScreenPoint(x, y)
                if (centerPosition != null) {
                    Log.d(
                        "KakaoMapFragment",
                        "Lat = ${centerPosition.latitude}\nLng = ${centerPosition.longitude}"
                    )
                    tvMapViewLatLng.text =
                        "Lat = ${centerPosition.latitude}\nLng = ${centerPosition.longitude}"
                }
                // toScreenPoint() 를 이용하여 지리적 좌표를 스크린 좌표로 변환할 수 있습니다.
                val point = map.toScreenPoint(centerPosition)

                val labelManager = map.labelManager
                if (labelManager != null) {
                    val layer = labelManager.layer
                    if (layer != null) {
                        if (point != null) {
                            label =
                                layer.addLabel(
                                    LabelOptions.from(map.fromScreenPoint(point.x, point.y))
                                        .setStyles(R.drawable.green_marker)
                                )
                        }
                    }
                }
            }
            "position" -> {
                // fromScreenPoint() 를 이용하여 스크린 좌표를 지리적 좌표로 변환할 수 있습니다.
                val position = map.fromScreenPoint(x, y)
                label.moveTo(position)

                if (position != null) {
                    kakaoMapViewModel.setSelectedPosition(position.latitude, position.longitude)
                    Log.d(
                        "KakaoMapFragment",
                        "Lat = ${position.latitude}\nLng = ${position.longitude}"
                    )
                    tvMapViewLatLng.text = "Lat = ${position.latitude}\nLng = ${position.longitude}"
                }
            }
        }
    }

    private fun setupLocation(map: KakaoMap): LatLng {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(
                requireActivity()
            ) // 이미 모든 권한이 허용된 상태일 경우 위치 정보 가져오기 시도
        if (
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1000
            )
        }
        var latLng = LatLng.from(37.497838, 127.027576)
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location?
            -> // Got last known location. In some rare situations this can be null.
            // 최근 위치를 가져오는 데 성공했을 때의 동작
            if (location != null) { // 위치를 사용할 수 있음
                latLng = LatLng.from(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newCenterPosition(latLng))
            }
        }
        return latLng
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
