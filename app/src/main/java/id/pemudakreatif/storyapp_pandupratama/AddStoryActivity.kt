package id.pemudakreatif.storyapp_pandupratama

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import id.pemudakreatif.storyapp_pandupratama.ApiClient.apiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException

class AddStoryActivity : AppCompatActivity() {
    private lateinit var selectedImageUri: Uri
    private lateinit var ivSelectedPhoto: ImageView
    private lateinit var edAddDescription: EditText
    private lateinit var buttonAdd: Button
    private lateinit var buttonSelectPhoto: Button
    private lateinit var progressBar: ProgressBar
    private val REQUEST_CODE_IMAGE_PICK = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        ivSelectedPhoto = findViewById(R.id.iv_selected_photo)
        edAddDescription = findViewById(R.id.ed_add_description)
        buttonAdd = findViewById(R.id.button_add)
        buttonSelectPhoto = findViewById(R.id.button_select_photo)
        progressBar = findViewById(R.id.progress_bar)

        checkStoragePermission()

        buttonSelectPhoto.setOnClickListener {
            openGalleryForImage()
        }

        buttonAdd.setOnClickListener {
            val description = edAddDescription.text.toString()
            if (description.isNotEmpty() && ::selectedImageUri.isInitialized) {
                uploadStory(description, selectedImageUri)
            } else {
                Toast.makeText(this, "Mohon lengkapi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    true
                }
                R.id.nav_add_story -> {
                    true
                }
                R.id.nav_logout -> {
                    SessionManager.clearSession(this)
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE_PICK && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    selectedImageUri = uri
                    ivSelectedPhoto.setImageURI(uri)
                } catch (e: Exception) {
                    Log.e("AddStoryActivity", "Error setting image URI", e)
                    Toast.makeText(this, "Gagal memuat gambar.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadStory(description: String, photoUri: Uri) {
        progressBar.visibility = View.VISIBLE
        val token = SessionManager.getToken(this)
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        val lat = "0.0"
        val lon = "0.0"

        val file = getFileFromUri(photoUri)
        if (file == null || !file.exists()) {
            Log.e("AddStoryActivity", "File tidak ditemukan.")
            Toast.makeText(this, "File tidak ditemukan", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        val maxSize = 1 * 1024 * 1024
        if (file.length() > maxSize) {
            Toast.makeText(this, "File terlalu besar. Maksimal 1MB.", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        val mimeType = when (file.extension.lowercase()) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> "application/octet-stream"
        }

        val requestBody = RequestBody.create(MediaType.parse(mimeType), file)
        val photoPart = MultipartBody.Part.createFormData("photo", file.name, requestBody)
        val descriptionPart = RequestBody.create(MediaType.parse("text/plain"), description)
        val latPart = RequestBody.create(MediaType.parse("text/plain"), lat)
        val lonPart = RequestBody.create(MediaType.parse("text/plain"), lon)

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val response = apiService.addStory("Bearer $token", descriptionPart, photoPart, latPart, lonPart)
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Cerita berhasil ditambahkan!"
                    Toast.makeText(this@AddStoryActivity, message, Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@AddStoryActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Gagal menambahkan cerita"
                    Toast.makeText(this@AddStoryActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("AddStoryActivity", "Error: ${e.message}", e)
                Toast.makeText(this@AddStoryActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val resolver = contentResolver
        val inputStream = resolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)

        try {
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            Log.e("AddStoryActivity", "Failed to copy file: ${e.message}")
            return null
        }

        return tempFile
    }

    override fun onBackPressed() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_add_story
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

}
