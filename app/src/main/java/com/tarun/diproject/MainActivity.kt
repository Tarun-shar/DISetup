package com.tarun.diproject

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tarun.diproject.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        adapter = PostAdapter(emptyList())
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // Collect posts from ViewModel
        lifecycleScope.launch {
            viewModel.posts.collectLatest { posts ->
                adapter.updateData(posts)
            }
        }

        // Collect created posts (POST API)
        lifecycleScope.launch {
            viewModel.createdPost.collectLatest { created ->
                if (created.isNotEmpty()) {
                    val updatedList = viewModel.posts.value.toMutableList()
                    updatedList.add(0, created.last())
                    adapter.updateData(updatedList)
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }

        // Loading state
        lifecycleScope.launch {
            viewModel.loading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Error handling
        lifecycleScope.launch {
            viewModel.error.collectLatest { errorMsg ->
                if (errorMsg.isNotEmpty()) {
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle "Add Post" button click
        binding.btnAddPost.setOnClickListener {
            val title = binding.titleInput.text.toString().trim()
            val body = binding.bodyInput.text.toString().trim()

            if (title.isNotEmpty() && body.isNotEmpty()) {
                val newPost = com.tarun.diproject.Model.Post(
                    userId = 1,
                    id = 0,
                    title = title,
                    body = body
                )
                viewModel.addPost(newPost)
                binding.titleInput.text.clear()
                binding.bodyInput.text.clear()
                Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter both title and body", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
