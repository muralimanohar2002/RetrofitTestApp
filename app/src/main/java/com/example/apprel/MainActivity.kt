package com.example.apprel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.apprel.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var retrofit: Retrofit
    private lateinit var retrofitInterface: RetrofitInterface
    private val BASE_URL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        retrofitInterface = retrofit.create(RetrofitInterface::class.java)

        binding.loginButton.setOnClickListener {
            if(binding.email.text.toString().isEmpty() or binding.password.text.toString().isEmpty()){
                Toast.makeText(this, "Please fill all necessary fields", Toast.LENGTH_LONG).show()
            } else{
                loginHandle()
            }
        }

        binding.signup.setOnClickListener {
            signupHandle()
        }
    }

    private fun signupHandle() {
        val view: View = View.inflate(this, R.layout.signup_dialogue, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setView(view).show()

        var nameEdit: EditText = view.findViewById(R.id.nameEdit)
        var emailEdit: EditText = view.findViewById(R.id.emailEdit)
        var passwordEdit: EditText = view.findViewById(R.id.passwordEdit)
        var signUpBtn: Button = view.findViewById(R.id.signUpLongBtn)

        signUpBtn.setOnClickListener {
            var map = HashMap<String,String>()
            map["nameEdit"] = nameEdit.text.toString()
            map["emailEdit"] = emailEdit.text.toString()
            map["passwordEdit"] = passwordEdit.text.toString()

            var call: Call<Void> = retrofitInterface.executeSignup(map)
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if(response.code()==200){
                        Toast.makeText(this@MainActivity, "Successfully registered", Toast.LENGTH_LONG).show()
                    } else if(response.code()==400){
                        Toast.makeText(this@MainActivity,"Already registered", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }


    private fun loginHandle() {
        var map = HashMap<String,String>()
        map["email"] = binding.email.text.toString()
        map["password"] = binding.password.text.toString()

        var call: Call<LoginResult> = retrofitInterface.executeLogin(map)
        call.enqueue(object : Callback<LoginResult?> {
            override fun onResponse(
                call: Call<LoginResult?>,
                response: Response<LoginResult?>
            ) {
                if(response.code()==200){
                    var loginResult = response.body()
                    var intent = Intent(this@MainActivity, TeamCreate::class.java)
                    intent.putExtra("name", loginResult?.name)
                    intent.putExtra("email", loginResult?.email)
                    startActivity(intent)
                } else if(response.code()==404){
                    Toast.makeText(this@MainActivity, "Enter correct credentials", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginResult?>, t: Throwable) {
                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }
}