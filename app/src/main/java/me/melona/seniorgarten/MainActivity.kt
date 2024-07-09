package me.melona.seniorgarten

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.melona.seniorgarten.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedUserName = getUserName(this)
        if (savedUserName != null) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            //        val appBarConfiguration = AppBarConfiguration(
            //            setOf(
            //                R.id.navigation_home, R.id.navigation_dashboard,
            // R.id.navigation_notifications
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

    private fun getUserName(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userName", null)
    }
}
