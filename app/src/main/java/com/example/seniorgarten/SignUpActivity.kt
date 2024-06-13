package com.example.seniorgarten

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isInvisible
import com.example.seniorgarten.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText = binding.editText

        editText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)
            ) {
                // 여기에 완료 버튼이 눌렸을 때 실행할 코드를 작성합니다.
                binding.textView.visibility = View.GONE
                binding.textView2.visibility = View.GONE
                binding.editText.visibility = View.GONE
                binding.textView4.visibility = View.VISIBLE

                val name = binding.editText.text.toString()
                val greeting = "${name}님이시군요!"
                binding.textView4.text = greeting
                saveUserName(this, name)

                val delayMillis = 1000L // 2초 딜레이
                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    // 실행할 코드 작성
                    // 예시: Toast 메시지 출력
                    binding.textView5.visibility = View.VISIBLE

                    // 메인 화면으로 이동
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                handler.postDelayed(runnable, delayMillis)
                // 로그인 성공 시
                saveLoginState(this, true)

                true // 이벤트가 처리되었음을 반환
            } else {
                false // 이벤트를 처리하지 않음을 반환
            }
        }

    }

    private fun saveUserName(context: Context, name: String) {
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userName", name)
        editor.apply()
    }

    fun saveLoginState(context: Context, isLoggedIn: Boolean) {
        val sharedPreferences = context.getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}