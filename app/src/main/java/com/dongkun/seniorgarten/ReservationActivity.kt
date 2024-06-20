package com.dongkun.seniorgarten

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.dongkun.seniorgarten.databinding.ActivityReservationBinding
import com.kakao.vectormap.LatLng
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReservationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReservationBinding

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
        var centerPosition: LatLng
        // KakaoMapViewModel의 LiveData를 관찰하여 UI 업데이트
        val viewModel: KakaoMapViewModel =
            ViewModelProvider(this).get(KakaoMapViewModel::class.java)
        Log.d("ReservationActivity", viewModel.selectedPosition.hasActiveObservers().toString())
        viewModel.selectedPosition.observe(this) { latLng ->
            Log.d("ReservationActivity", "Observed value: $latLng")
            val apiClient = ApiClient()
            if (latLng != null) {
//                centerPosition = Pair(latLng.first, latLng.second)
                apiClient.getAddressFromCoordinates(latLng.first, latLng.second) { address ->
                    if (address != null) {
                        Log.d("MainActivity", "주소 찾음: $address")
                        runOnUiThread {
                            // UI 업데이트 등의 작업을 수행할 수 있음
                            binding.textView7.text = address

                        }
                    } else {
                        Log.e("MainActivity", "주소를 찾을 수 없습니다.")
                    }
                }

            } else {
                Log.d("ReservationActivity", "Observed value is null")
            }
        }
        binding.button5.visibility = View.VISIBLE
        val getCurrentDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val getCurrentTime =
            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
//        insertReservation(
//            getCurrentDate.toString(),
//            getCurrentTime.toString(),
//            centerPosition.latitude,
//            centerPosition.longitude
//        )

    }

    private fun insertReservation(date: String, time: String, latitude: Double, longitude: Double) {
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
}