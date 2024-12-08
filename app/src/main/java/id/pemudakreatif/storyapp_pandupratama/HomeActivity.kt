package id.pemudakreatif.storyapp_pandupratama

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.core.util.Pair
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.flow.collectLatest

class HomeActivity : AppCompatActivity() {
    private lateinit var stories: List<Story>
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var welcomeText: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
//        recyclerView = findViewById(R.id.rv_story_list)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        stories = mutableListOf()
//        storyAdapter = StoryAdapter(stories) { story ->
//            val intent = Intent(this, StoryDetailActivity::class.java)
//            intent.putExtra("story_id", story.id)
//            startActivity(intent)
//        }
//        recyclerView.adapter = storyAdapter

        welcomeText = findViewById(R.id.welcomeText)
        val userName = SessionManager.getUserName(this) ?: "User"
        welcomeText.text = "Selamat Datang, $userName"

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (javaClass.simpleName != "HomeActivity") {
                        val intent = Intent(this, HomeActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    true
                }
                R.id.nav_add_story -> {
                    if (javaClass.simpleName != "AddStoryActivity") {
                        val intent = Intent(this, AddStoryActivity::class.java)
                        startActivity(intent)
                    }
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

        recyclerView = findViewById(R.id.rv_story_list)
        welcomeText = findViewById(R.id.welcomeText)
        progressBar = findViewById(R.id.progress_bar)

        // Set layout manager untuk RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi StoryAdapter dengan lambda untuk menangani klik
        storyAdapter = StoryAdapter { story ->
            val intent = Intent(this, StoryDetailActivity::class.java)
            intent.putExtra("story_id", story.id)
            startActivity(intent)
        }

        recyclerView.adapter = storyAdapter

        loadStories()

    }

    private fun loadStories() {
        progressBar.visibility = View.VISIBLE
        val token = SessionManager.getToken(this)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            return
        }

        val apiService = ApiClient.apiService
        val pager = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        )

        lifecycleScope.launch {
            pager.flow.collectLatest { pagingData ->
                storyAdapter.submitData(pagingData)
                progressBar.visibility = View.GONE
            }
        }
    }
//    private fun loadStories() {
//        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
//        progressBar.visibility = View.VISIBLE
//        val token = SessionManager.getToken(this)
//        if (token.isNullOrEmpty()) {
//            Toast.makeText(this, "Token tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
//            return
//        }
//        val apiService = ApiClient.apiService
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                val response = apiService.getAllStories("Bearer $token")
//                progressBar.visibility = View.GONE
//                if (response.isSuccessful && response.body() != null) {
//                    val listStory = response.body()!!.listStory
//                    if (listStory.isNotEmpty()) {
//                        stories = listStory
//
//                        storyAdapter = StoryAdapter(stories) { story ->
//                            val intent = Intent(this@HomeActivity, StoryDetailActivity::class.java)
//                            intent.putExtra("story_id", story.id)
//
//                            val imageView = recyclerView.findViewHolderForAdapterPosition(stories.indexOf(story))?.itemView?.findViewById<ImageView>(R.id.iv_item_photo)
//                            val textView = recyclerView.findViewHolderForAdapterPosition(stories.indexOf(story))?.itemView?.findViewById<TextView>(R.id.tv_item_name)
//
//                            if (imageView != null && textView != null) {
//                                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                                    this@HomeActivity,
//                                    Pair(imageView, "story_image"),
//                                    Pair(textView, "story_name")
//                                )
//                                startActivity(intent, options.toBundle())
//                            }
//                        }
//
//                        recyclerView.adapter = storyAdapter
//                        storyAdapter.notifyDataSetChanged()
//                    } else {
//                        Toast.makeText(this@HomeActivity, "Tidak ada cerita yang ditemukan", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Toast.makeText(this@HomeActivity, "Gagal memuat cerita", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                progressBar.visibility = View.GONE
//                Log.e("HomeActivity", "Error loading stories: ${e.message}")
//                Toast.makeText(this@HomeActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    override fun onBackPressed() {
        if (isTaskRoot) {
            super.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }
    override fun onResume() {
        super.onResume()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.selectedItemId = R.id.nav_home
        loadStories()
    }

}