package com.example.androidstudiopractice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class Loginpage : AppCompatActivity() {
    var username : String = ""
    var password :String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginpage)
        val gson = GsonBuilder().setLenient().create()
        //builder패턴
        val retrofit = Retrofit.Builder()
            .baseUrl("http:/컴퓨터ip주소/")
            //서버와 통신을할ㄸ ㅐ중간에 시리얼라이져 작업을하는데 그떄 마지막 최종값을 무엇으로 받을건지 ()안에 넣어줘야한다
            //JSON형태를 원하는데 그작업을 해주는친구가 Gson이다 GsonConverterFactory를 적어야한다
            //서버 ->읽을수 없는 데이터 -> JSON -> Gson
            //Gson > 읽을수 없는데이터를 코틀린 객체로 바꿔준다

            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        (findViewById<TextView>(R.id.signup)).apply{
            this.setOnClickListener{
                val intent : Intent = Intent(this@Loginpage,SignUpPage::class.java)
                startActivity(intent)
            }
        }
        //아래에 코드는 EditText박스에서 사용자가 입력하는값을 실시간으로 받는 코드이다 (doAfterTextChanged)
        findViewById<EditText>(R.id.id).doAfterTextChanged {
            username = it.toString()
        }
        findViewById<EditText>(R.id.password).doAfterTextChanged {
            password = it.toString()
        }
        //로그인TextView가 눌리면 입력받은 비밀번호를 Hash함수로 바꿔주고 넣어준다
        findViewById<TextView>(R.id.login).setOnClickListener {
            val hashpassword = hashPassword(password)
            retrofitService.checkLogin(username,hashpassword).enqueue(object :
                Callback<LoginCheckResponse> {
                override fun onResponse(call: Call<LoginCheckResponse>, response: Response<LoginCheckResponse>) {
                    val re = response.body()
                    if (re != null) {
                        Log.d("testt","${re.message}")
                    }
                }
                override fun onFailure(call: Call<LoginCheckResponse>, t: Throwable) {
                    Log.e("MySignUp", "Retrofit request failed: ${t.message}")
                }
            })

        }
    }
    fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}