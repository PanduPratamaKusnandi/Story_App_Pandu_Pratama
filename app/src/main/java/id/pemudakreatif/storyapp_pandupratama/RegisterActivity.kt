package id.pemudakreatif.storyapp_pandupratama

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.pemudakreatif.storyapp_pandupratama.customview.EyeIconView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginTextView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nameEditText = findViewById(R.id.ed_register_name)
        emailEditText = findViewById(R.id.ed_register_email)
        passwordEditText = findViewById(R.id.ed_register_password)
        registerButton = findViewById(R.id.btn_register)
        loginTextView = findViewById(R.id.tv_login)
        progressBar = findViewById(R.id.progress_bar)

        val nameErrorTextView = findViewById<TextView>(R.id.tv_name_error)
        val emailErrorTextView = findViewById<TextView>(R.id.tv_email_error)
        val passwordErrorTextView = findViewById<TextView>(R.id.tv_password_error)
        val eyeIconView = findViewById<EyeIconView>(R.id.eye_icon)

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            var isValid = true

            if (name.isEmpty()) {
                nameErrorTextView.text = "Nama Lengkap tidak boleh kosong"
                nameErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else {
                nameErrorTextView.visibility = View.GONE
            }

            if (email.isEmpty()) {
                emailErrorTextView.text = "Email tidak boleh kosong"
                emailErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else if (!isValidEmail(email)) {
                emailErrorTextView.text = "Format email tidak valid"
                emailErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else {
                emailErrorTextView.visibility = View.GONE
            }

            if (password.isEmpty()) {
                passwordErrorTextView.text = "Kata Sandi tidak boleh kosong"
                passwordErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else if (password.length < 8) {
                passwordErrorTextView.text = "Kata Sandi minimal 8 karakter"
                passwordErrorTextView.visibility = View.VISIBLE
                isValid = false
            } else {
                passwordErrorTextView.visibility = View.GONE
            }

            if (isValid) {
                registerUser(name, email, password)
            }
        }

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

        loginTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val registerRequest = RegisterRequest(name, email, password)
        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.register(registerRequest)
            progressBar.visibility = View.GONE
            if (response.isSuccessful) {
                Toast.makeText(this@RegisterActivity, "Pendaftaran Berhasil, Silahkan Masuk!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@RegisterActivity, "Pendaftaran Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
