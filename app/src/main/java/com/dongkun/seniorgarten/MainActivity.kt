package com.dongkun.seniorgarten

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dongkun.seniorgarten.databinding.ActivityMainBinding
import com.kakao.vectormap.utils.MapUtils.getHashKey
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedUserName = getUserName(this)
        if (savedUserName != null) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // database test
            dbTest()

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
//            )
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

        } else {
            // 사용자 이름이 저장되어 있지 않다면 SignUpActivity로 이동합니다.
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish() // MainActivity 종료
        }

    }

    private fun checkLoginState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun dbTest() {
        val thread = Thread {
            // MySQL 데이터베이스 연결 정보
            val url = "jdbc:mysql://220.81.153.234:2873/dbdbdb"
            val username = "root"
            val password = "iamgroot"

            // MySQL JDBC 드라이버 클래스 이름
            val driver = "com.mysql.jdbc.Driver"

            // 데이터베이스 연결 객체
            var connection: Connection? = null

            try {
                // JDBC 드라이버 로드
                Class.forName(driver)

                // 데이터베이스 연결 시도
                connection = DriverManager.getConnection(url, username, password)

                // 연결 성공 메시지 출력
                val userName = getUserName(this)
                val welcomeMessage = "${userName}님, 환영합니다!"

                runOnUiThread {
                    // Toast로 웰컴 메시지를 표시합니다.
                    Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()

                }
                println("데이터베이스에 성공적으로 연결되었습니다.")
            } catch (e: SQLException) {
                val errorMessage = "데이터베이스 연결 실패: ${e.message}"
                runOnUiThread {
                    // Toast로 웰컴 메시지를 표시합니다.
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()

                }
                // 연결 실패 시 예외 처리
                println("데이터베이스 연결 실패: ${e.message}")
            } finally {
                // 연결 종료
                connection?.close()
            }

        }

        thread.start()
        try {
            thread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun getUserName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName", null)
    }
}