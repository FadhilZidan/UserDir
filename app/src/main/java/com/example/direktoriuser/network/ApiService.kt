package com.example.direktoriuser.network

import com.example.direktoriuser.data.Post
import com.example.direktoriuser.data.PostBaru
import com.example.direktoriuser.data.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}/posts")
    suspend fun getPostsByUser(@Path("id") userId: Int): List<Post>

    @POST("posts")
    suspend fun buatPost(@Body post: PostBaru): Post
}
