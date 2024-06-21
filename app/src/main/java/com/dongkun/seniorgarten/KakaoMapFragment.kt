package com.dongkun.seniorgarten

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dongkun.seniorgarten.databinding.FragmentKakaoMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
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
    private var isContinue = false

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest

    private lateinit var label: Label
    private lateinit var tvMapViewLatLng: TextView
    private lateinit var map: KakaoMap
    private lateinit var kakaoMapViewModel: KakaoMapViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        kakaoMapViewModel = ViewModelProvider(requireActivity()).get(KakaoMapViewModel::class.java)

        _binding = FragmentKakaoMapBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //        val textView: TextView = binding.textDashboard
        //        dashboardViewModel.text.observe(viewLifecycleOwner) {
        //            textView.text = it
        //        }
        tvMapViewLatLng = binding.tvMapViewLatlng
        val mapView: MapView = binding.mapView
        val locationPermissionRequest =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                    permissions ->
                when {
                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        false
                    ) -> { // Precise location access granted.
                        setupMapView(mapView)
                    }
                    permissions.getOrDefault(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        false
                    ) -> { // Only approximate location access granted.
                        Toast.makeText(
                            requireActivity(),
                            "ACCESS_COARSE_LOCATION",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> { // No location access granted.
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

    private fun setupMapView(mapView: MapView) {
        mapView.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}

                override fun onMapError(p0: Exception?) {}
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(p0: KakaoMap) { //                setupReservationButton(p0)
                    setupLocation(p0)
                    updateMapPosition(p0, false)

                    p0.setOnCameraMoveEndListener { _, _, _ -> updateMapPosition(p0, true) }

                    map = p0
                }

                override fun getPosition(): LatLng {
                    return LatLng.from(36.5869233979831, 128.187405044754)
                }
            }
        )
    }

    private fun updateMapPosition(map: KakaoMap, b: Boolean) {
        val viewport = map.viewport
        val x = viewport.width() / 2
        val y = viewport.height() / 2

        val position = map.fromScreenPoint(x, y)
        if (b) {
            label.moveTo(position)
        }
        if (position != null) { // ViewModel을 통해 위치 정보 저장
            kakaoMapViewModel.setSelectedPosition(position.latitude, position.longitude)
            val latLngText =
                getString(R.string.lat_lng_format, position.latitude, position.longitude)
            if (Constants.IS_DEBUG) {
                tvMapViewLatLng.text = latLngText
            } else {
                tvMapViewLatLng.text = "지도를 움직여서 주소를 설정할 수 있어요!"
            }
        }
        if (!b) {
            val point = map.toScreenPoint(position)

            val labelLayer = map.labelManager?.layer
            if (labelLayer != null) { // labelLayer가 null이 아닌 경우에만 addLabel() 메서드를 호출합니다.
                if (point != null) {
                    label =
                        labelLayer.addLabel(
                            LabelOptions.from(map.fromScreenPoint(point.x, point.y))
                                .setStyles(R.drawable.green_marker)
                        )
                }
            }
        }
    }

    private fun setupLocation(map: KakaoMap) {
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
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location?
                -> // Got last known location. In some rare situations this can be null.
                // 최근 위치를 가져오는 데 성공했을 때의 동작
                if (location != null) { // 위치를 사용할 수 있음
                    val latitude = location.latitude
                    val longitude =
                        location
                            .longitude // 여기서 가져온 위치를 사용할 수 있습니다. // 이 시점에서 latitude와 longitude에 값이
                    // 설정됩니다. // 이제 이 값을 외부에서 사용할 수 있습니다. // 예: 다른 함수에서 사용하거나 변수에
                    // 할당할 수 있습니다.
                    map.moveCamera(
                        CameraUpdateFactory.newCenterPosition(LatLng.from(latitude, longitude))
                    )
                }
            }
        } //        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
