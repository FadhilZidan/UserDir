package com.example.direktoriuser

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.direktoriuser.data.PostBaru
import com.example.direktoriuser.network.RetrofitClient
import kotlinx.coroutines.launch

class PostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXTRA_USER_NAME = "EXTRA_USER_NAME"
    }

    private var userId: Int = 1
    private var userName: String = "LEANNE GRAHAM"

    private lateinit var tvUserHeaderTag: TextView
    private lateinit var tvHeaderSubtitle: TextView
    private lateinit var etPostTitle: EditText
    private lateinit var btnTambahPost: TextView
    private lateinit var rvPosts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutErrorDialog: View
    private lateinit var btnTutup: TextView
    private lateinit var btnCobaLagi: TextView

    private val postAdapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.postMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userId = intent.getIntExtra(EXTRA_USER_ID, 1)
        userName = intent.getStringExtra(EXTRA_USER_NAME) ?: "LEANNE GRAHAM"

        initViews()
        setupRecyclerView()
        setupListeners()

        muatPosts()
    }

    private fun initViews() {
        tvUserHeaderTag = findViewById(R.id.tvUserHeaderTag)
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle)
        etPostTitle = findViewById(R.id.etPostTitle)
        btnTambahPost = findViewById(R.id.btnTambahPost)
        rvPosts = findViewById(R.id.rvPosts)
        progressBar = findViewById(R.id.progressBar)
        layoutErrorDialog = findViewById(R.id.layoutErrorDialog)

        val dialogView = layoutErrorDialog
        btnTutup = dialogView.findViewById(R.id.btnTutup)
        btnCobaLagi = dialogView.findViewById(R.id.btnCobaLagi)

        tvUserHeaderTag.text = userName.uppercase()
    }

    private fun setupRecyclerView() {
        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = postAdapter
    }

    private fun setupListeners() {
        btnTambahPost.setOnClickListener {
            val title = etPostTitle.text.toString().trim()
            if (title.isEmpty()) {
                Toast.makeText(this, "Judul postingan tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                tambahPost(title)
            }
        }

        btnCobaLagi.setOnClickListener {
            muatPosts()
        }

        btnTutup.setOnClickListener {
            layoutErrorDialog.visibility = View.GONE
        }
    }

    private fun muatPosts() {
        progressBar.visibility = View.VISIBLE
        layoutErrorDialog.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.api.getPostsByUser(userId)
                postAdapter.updateData(posts)
                tvHeaderSubtitle.text = getString(R.string.postingan_format, posts.size)
            } catch (e: Exception) {
                e.printStackTrace()
                tvHeaderSubtitle.text = getString(R.string.status_gagal_memuat)
                layoutErrorDialog.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun tambahPost(title: String) {
        btnTambahPost.isEnabled = false
        btnTambahPost.alpha = 0.6f

        lifecycleScope.launch {
            try {
                val postBaru = PostBaru(
                    title = title,
                    body = "Isi postingan baru",
                    userId = userId,
                )
                val responsePost = RetrofitClient.api.buatPost(postBaru)
                postAdapter.addPostAtTop(responsePost)
                rvPosts.scrollToPosition(0)
                etPostTitle.text.clear()
                tvHeaderSubtitle.text = getString(R.string.postingan_format, postAdapter.itemCount)
                Toast.makeText(this@PostActivity, "Postingan ditambahkan! ID: ${responsePost.id}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@PostActivity, "Gagal menambah postingan: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } finally {
                btnTambahPost.isEnabled = true
                btnTambahPost.alpha = 1.0f
            }
        }
    }
}
