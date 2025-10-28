package com.tarun.diproject

import com.tarun.diproject.API.ApiServices
import com.tarun.diproject.Model.Post
import jakarta.inject.Inject

class PostRepository @Inject constructor(private val apiService: ApiServices) {
    suspend fun getPosts(): List<Post> = apiService.getPosts()

    suspend fun createPosts(post: Post): Post = apiService.createPost(post)
}
