package com.tarun.diproject.API

import com.tarun.diproject.Model.Post
import retrofit2.http.GET

interface ApiServices {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}