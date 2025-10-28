package com.tarun.diproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tarun.diproject.Model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PostViewModel @Inject constructor(
    private val repository: PostRepository
) : ViewModel() {

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _createdPost = MutableStateFlow<List<Post>>(emptyList())
    val createdPost: StateFlow<List<Post>> = _createdPost.asStateFlow()

    init {
        fetchPosts()
    }

    private fun fetchPosts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _posts.value = repository.getPosts()
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }

    // 🔹 Add new post via API
    fun addPost(post: Post) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val newPost = repository.createPosts(post)
                _createdPost.value = listOf(newPost)

                // Optionally: Add the new post to your existing post list
                _posts.value = _posts.value + newPost

            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Unknown error"
            } finally {
                _loading.value = false
            }
        }
    }
}
