package com.example.androidstudiopractice

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class SignUpPage : AppCompatActivity() {
    var userId: String = ""
    var userPassword1: String = ""
    var userPassword2: String = ""
    var userName: String = ""
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)
        val gson = GsonBuilder().setLenient().create()
        (findViewById<TextView>(R.id.logingogo)).setOnClickListener {
            startActivity(Intent(this, Loginpage::class.java))
        }
        findViewById<EditText>(R.id.id_input).doAfterTextChanged {
            userId = it.toString()
        }
        findViewById<EditText>(R.id.password_input1).doAfterTextChanged {
            userPassword1 = it.toString()
        }
        findViewById<EditText>(R.id.password_input2).doAfterTextChanged {
            userPassword2 = it.toString()
        }
        findViewById<EditText>(R.id.name).doAfterTextChanged {
            userName = it.toString()
        }
        val textbox =  findViewById<TextView>(R.id.textbox)

        val retrofit = Retrofit.Builder()
            .baseUrl("http:/컴퓨터ip주소/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)
        findViewById<TextView>(R.id.signgogo).setOnClickListener {
            if(userPassword1 != userPassword2){
                //이제 비밀번호 유효성검사 코드 추가하기 유효성에안맞으면 text를 형식에맞춰주세요라고 만들면됨
                Toast.makeText(
                    this,"비밀번호가 다릅니다!", Toast.LENGTH_LONG
                ).show()
            }else{
                val hashedPassword = hashPassword(userPassword1)
                Log.d("testt","${hashedPassword}")
                textbox.visibility = View.INVISIBLE
                retrofitService.Register(userId,hashedPassword,userName)
                    .enqueue(object : Callback<Redata> {
                        override fun onResponse(call: Call<Redata>, response: Response<Redata>) {
                            val re = response.body()
                            if(re != null) {
                                Log.d("testt", "${re.userPassword}")
                            }
                        }
                        override fun onFailure(call: Call<Redata>, t: Throwable) {
                            Log.e("NetworkError","${t.message}")
                        }
                    })
            }
        }
        findViewById<TextView>(R.id.duplicate_check).setOnClickListener {
            Log.d("testt","눌림")
            retrofitService.checkDuplicateId(userId).enqueue(object : Callback<JsonElement> {
                override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                    if(response.isSuccessful) {
                        val jsonElement = response.body()
                        Log.d("testt", "아이디중복체크 연결성공")
                        if (jsonElement is JsonObject) {
                            val isDuplicate = jsonElement.get("isDuplicate").asBoolean
                            val message = jsonElement.get("message").asString
                            textbox.text = message
                            textbox.visibility = View.VISIBLE
                        }
                    }
                }
                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    Log.e("NetworkError","${t.message}")
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