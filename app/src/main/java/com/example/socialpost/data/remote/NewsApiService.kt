package com.example.socialpost.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NewsApiService {
    @GET("latest-news")
    suspend fun getTopHeadlines(
        @Header("Authorization") apiKey: String,
        @Query("category") category: String? = null,
        @Query("language") language: String = "en"
    ): CurrentsApiResponse

    @GET("search")
    suspend fun searchNews(
        @Header("Authorization") apiKey: String,
        @Query("keywords") query: String,
        @Query("language") language: String = "en"
    ): CurrentsApiResponse
}

data class CurrentsApiResponse(
    val status: String,
    val news: List<CurrentsArticle>?
)

data class CurrentsArticle(
    val id: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val author: String?,
    val image: String?,
    val language: String?,
    val category: List<String>?,
    val published: String?
)
