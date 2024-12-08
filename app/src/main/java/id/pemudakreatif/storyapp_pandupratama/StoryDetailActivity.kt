package id.pemudakreatif.storyapp_pandupratama

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var storyId: String
    private lateinit var nameTextView: TextView
    private lateinit var photoImageView: ImageView
    private lateinit var descriptionTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_detail)

        postponeEnterTransition()

        storyId = intent.getStringExtra("story_id") ?: ""
        nameTextView = findViewById(R.id.tv_detail_name)
        photoImageView = findViewById(R.id.iv_detail_photo)
        descriptionTextView = findViewById(R.id.tv_detail_description)

        val transitionImage = findViewById<ImageView>(R.id.iv_detail_photo)
        val transitionName = intent.getStringExtra("transition_name")
        if (transitionName != null) {
            ViewCompat.setTransitionName(transitionImage, transitionName)
        }

        loadStoryDetail()

        startPostponedEnterTransition()
    }
    private fun loadStoryDetail() {
        val token = SessionManager.getToken(this)
        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.Main) {
            val response = apiService.getStoryDetail(storyId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                val story = response.body()!!.story
                nameTextView.text = story.name
                Glide.with(this@StoryDetailActivity)
                    .load(story.photoUrl)
                    .into(photoImageView)
                descriptionTextView.text = story.description
            } else {
                Toast.makeText(this@StoryDetailActivity, "Gagal memuat detail cerita", Toast.LENGTH_SHORT).show()
            }
        }
    }
}