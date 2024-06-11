package com.example.seniorgarten

import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.seniorgarten.databinding.ActivityMainBinding
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    }

    private fun dbTest() {
//        val thread = Thread {
        var con: Connection? = null

        try {
            Class.forName("com.mysql.jdbc.Driver")
            val url = "jdbc:mysql://220.81.153.234:49156/dbdbdb"
            val user = "root"
            val passwd = "iamgroot"
            con = DriverManager.getConnection(url, user, passwd)
            Log.d("Database", con.toString())
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        // database operation
        var stmt: Statement? = null
        var rs: ResultSet? = null
        try {
            if (con != null) {
                stmt = con.createStatement()
                // 실행할 쿼리 작성
                val sql = "select * from MyTable"
                rs = stmt.executeQuery(sql)
                while (rs.next()) {
                    var name = rs.getString(1)
                    if (rs.wasNull()) name = "null"
                    var course_id = rs.getString(2)
                    if (rs.wasNull()) course_id = "null"
                    println(name + "\t" + course_id)
                }
            }
        } catch (e1: SQLException) {
            e1.printStackTrace()
        }
        try {
            if (stmt != null && !stmt.isClosed()) stmt.close()
        } catch (e1: SQLException) {
            e1.printStackTrace()
        }
    }

//        thread.start()
//        try {
//            thread.join()
//        } catch (e: InterruptedException) {
//            e.printStackTrace()
//        }
//    }
}