package id.pemudakreatif.storyapp_pandupratama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import id.pemudakreatif.storyapp_pandupratama.customview.EyeIconView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    private val apiService: ApiService = ApiClient.retrofit.create(ApiService::class.java)

    private lateinit var progressBar: ProgressBar
    private lateinit var loginErrorTextView: TextView
    private lateinit var emailErrorTextView: TextView
//    private lateinit var passwordErrorTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.ed_login_email)
        val passwordEditText = findViewById<EditText>(R.id.ed_login_password)
        val loginButton = findViewById<Button>(R.id.btn_login)
        val eyeIconView = findViewById<EyeIconView>(R.id.eye_icon)

        emailErrorTextView = findViewById(R.id.tv_email_error)
//        passwordErrorTextView = findViewById(R.id.tv_password_error)
        loginErrorTextView = findViewById(R.id.tv_login_error)

        val registerTextView = findViewById<TextView>(R.id.tv_register)
        registerTextView.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        progressBar = findViewById(R.id.progress_bar)

        eyeIconView.setOnClickListener {
            val isPasswordVisible = passwordEditText.transformationMethod == null
            if (isPasswordVisible) {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = null
            }
            passwordEditText.setSelection(passwordEditText.text.length)
            eyeIconView.togglePasswordVisibility()
        }

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailErrorTextView.text = "Format email salah"
                    emailErrorTextView.visibility = View.VISIBLE
                } else {
                    emailErrorTextView.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

//        passwordEditText.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable?) {
//                val password = s.toString()
//                if (password.length < 8) {
//                    passwordErrorTextView.text = "Kata sandi minimal 8 karakter"
//                    passwordErrorTextView.visibility = View.VISIBLE
//                } else {
//                    passwordErrorTextView.visibility = View.GONE
//                }
//            }
//
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//        })

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            emailErrorTextView.visibility = View.GONE
//            passwordErrorTextView.visibility = View.GONE

            if (email.isNotEmpty() && password.isNotEmpty() &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8) {
                loginUser(email, password)
            } else {
                if (email.isEmpty()) {
                    emailErrorTextView.text = "Email wajib diisi"
                    emailErrorTextView.visibility = View.VISIBLE
                }

//                if (password.isEmpty()) {
//                    passwordErrorTextView.text = "Kata Sandi wajib diisi"
//                    passwordErrorTextView.visibility = View.VISIBLE
//                }

                if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailErrorTextView.text = "Format email salah"
                    emailErrorTextView.visibility = View.VISIBLE
                }

//                if (password.isNotEmpty() && password.length < 8) {
//                    passwordErrorTextView.text = "Kata sandi minimal 8 karakter"
//                    passwordErrorTextView.visibility = View.VISIBLE
//                }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        progressBar.visibility = View.VISIBLE
        loginErrorTextView.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = apiService.login(loginRequest)

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE

                    if (response.isSuccessful && response.body()?.error == false) {
                        val loginResult = response.body()?.loginResult
                        if (loginResult != null) {
                            val token = loginResult.token
                            val userName = loginResult.name
                            val userId = loginResult.userId
                            Toast.makeText(this@LoginActivity, "Berhasil Masuk!", Toast.LENGTH_SHORT).show()

                            SessionManager.saveToken(this@LoginActivity, token, userId, userName)

                            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                            finish()
                        }
                    } else {
                        loginErrorTextView.text = "Gagal Masuk: ${response.body()?.message}"
                        loginErrorTextView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    loginErrorTextView.text = "Terjadi kesalahan: ${e.message}"
                    loginErrorTextView.visibility = View.VISIBLE
                }
            }
        }
    }
}

