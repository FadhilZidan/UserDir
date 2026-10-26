package com.example.direktoriuser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.direktoriuser.data.User
import com.example.direktoriuser.network.RetrofitClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var tvHeaderSubtitle: TextView
    private lateinit var rvUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutErrorDialog: View
    private lateinit var btnTutup: TextView
    private lateinit var btnCobaLagi: TextView

    private lateinit var userAdapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        setupRecyclerView()
        setupListeners()

        muatUsers()
    }

    private fun initViews() {
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle)
        rvUsers = findViewById(R.id.rvUsers)
        progressBar = findViewById(R.id.progressBar)
        layoutErrorDialog = findViewById(R.id.layoutErrorDialog)

        btnTutup = layoutErrorDialog.findViewById(R.id.btnTutup)
        btnCobaLagi = layoutErrorDialog.findViewById(R.id.btnCobaLagi)
    }

    private fun setupRecyclerView() {
        userAdapter = UserAdapter { user ->
            bukaPostActivity(user)
        }
        rvUsers.layoutManager = LinearLayoutManager(this)
        rvUsers.adapter = userAdapter
    }

    private fun setupListeners() {
        btnCobaLagi.setOnClickListener {
            muatUsers()
        }

        btnTutup.setOnClickListener {
            layoutErrorDialog.visibility = View.GONE
        }
    }

    private fun bukaPostActivity(user: User) {
        val intent = Intent(this, PostActivity::class.java).apply {
            putExtra(PostActivity.EXTRA_USER_ID, user.id)
            putExtra(PostActivity.EXTRA_USER_NAME, user.name)
        }
        startActivity(intent)
    }

    private fun muatUsers() {
        progressBar.visibility = View.VISIBLE
        layoutErrorDialog.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val users = RetrofitClient.api.getUsers()
                userAdapter.updateData(users)
                tvHeaderSubtitle.text = getString(R.string.pengguna_dimuat_format, users.size)
            } catch (e: Exception) {
                e.printStackTrace()
                tvHeaderSubtitle.text = getString(R.string.status_gagal_memuat)
                layoutErrorDialog.visibility = View.VISIBLE
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }
}
