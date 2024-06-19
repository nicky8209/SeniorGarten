package com.dongkun.seniorgarten

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dongkun.seniorgarten.databinding.ActivityReservationBinding
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
import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var label: Label
    private lateinit var tvMapViewLatLng: TextView
    private lateinit var map: KakaoMap

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됐을 때의 처리
                setupLocation(map)
            } else {
                // 권한이 거부됐을 때의 처리
                // 예: 사용자에게 앱의 기능에 대한 설명을 보여줌
                // 또는 필요한 경우 사용자를 설정 화면으로 리디렉션
            }
        }
    }

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
//        tvMapViewLatLng = binding.tvMapViewLatlng
//        val mapView: MapView = binding.mapView
//        setupMapView(mapView)
    }

    private fun setupMapView(mapView: MapView) {
        mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {

            }

            override fun onMapError(p0: Exception?) {

            }

        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(p0: KakaoMap) {
                setupReservationButton(p0)
                setupLocation(p0)
                updateMapPosition(p0, false)

                p0.setOnCameraMoveEndListener { _, _, _ ->
                    updateMapPosition(p0, true)

                }
                map = p0
            }

            override fun getPosition(): LatLng {
                return LatLng.from(36.5869233979831, 128.187405044754)
            }
//            36.5869233979831, 128.187405044754
        })

    }

    private fun setupReservationButton(map: KakaoMap) {
//        binding.button5.setOnClickListener {
//            val intent = Intent(this, SelectionActtivity::class.java)
//            startActivity(intent)
//            finish()
        // 현재 지도의 중심 좌표를 가져옵니다.
        val centerPosition = getCurrentMapCenter(map)
        val currentDate = Date()
        val getCurrentDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(currentDate)
        val getCurrentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentDate)
        if (centerPosition != null) {
            insertReservation(
                getCurrentDate.toString(),
                getCurrentTime.toString(),
                centerPosition.latitude,
                centerPosition.longitude
            )

        }
    }

    private fun insertReservation(date: String, time: String, latitude: Double, longitude: Double) {
        binding.button5.visibility = View.VISIBLE
        binding.button5.setOnClickListener {
            val thread = Thread {
                val url = "jdbc:mysql://220.81.153.234:2873/dbdbdb"
                val username = "root"
                val password = "iamgroot"
                val driver = "com.mysql.jdbc.Driver"
                var connection: Connection? = null
                val query =
                    "INSERT INTO reservation (date, time, latitude, longitude) VALUES (?, ?, ?, ?)"

                try {
                    Class.forName(driver)
                    connection = DriverManager.getConnection(url, username, password)
                    val preparedStatement = connection.prepareStatement(query)
                    preparedStatement.setString(1, date)
                    preparedStatement.setString(2, time)
                    preparedStatement.setDouble(3, latitude)
                    preparedStatement.setDouble(4, longitude)
                    preparedStatement.executeUpdate()

                    val userName = this.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
                        .getString("userName", null)
                    val welcomeMessage = "${userName}님, 예약이 완료되었습니다!"
                    runOnUiThread {
                        Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()
                    }
                    println("예약이 데이터베이스에 성공적으로 추가되었습니다.")
                } catch (e: SQLException) {
                    val errorMessage = "예약 추가 실패: ${e.message}"
                    runOnUiThread {
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                    }
                    println("예약 추가 실패: ${e.message}")
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                } finally {
                    connection?.close()
                }
            }
            thread.start()
//            try {
//                thread.join()
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }

        }

    }

    private fun getCurrentMapCenter(map: KakaoMap): LatLng? {
        val viewport = map.viewport
        val x = viewport.width() / 2
        val y = viewport.height() / 2
        return map.fromScreenPoint(x, y)

    }

    private fun setupLocation(map: KakaoMap) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 1000
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            // Got last known location. In some rare situations this can be null.
            // 최근 위치를 가져오는 데 성공했을 때의 동작
            if (location != null) {
                // 위치를 사용할 수 있음
                val latitude = location.latitude
                val longitude = location.longitude
                // 여기서 가져온 위치를 사용할 수 있습니다.
                // 이 시점에서 latitude와 longitude에 값이 설정됩니다.
                // 이제 이 값을 외부에서 사용할 수 있습니다.
                // 예: 다른 함수에서 사용하거나 변수에 할당할 수 있습니다.
                map.moveCamera(
                    CameraUpdateFactory.newCenterPosition(
                        LatLng.from(
                            latitude, longitude
                        )
                    )
                )
            }
        }

    }

    private fun updateMapPosition(map: KakaoMap, b: Boolean) {
        val viewport = map.viewport
        val x = viewport.width() / 2
        val y = viewport.height() / 2

        val position = map.fromScreenPoint(x, y)
        if (b) {
            label.moveTo(position)

        }
        if (position != null) {
            tvMapViewLatLng.text =
                "Lat = ${position.latitude}\nLng = ${position.longitude}"

        }
        if (!b) {
            val point = map.toScreenPoint(position)

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

        }


    }
}