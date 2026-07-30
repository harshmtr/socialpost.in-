package com.example.socialpost.data.model

data class NewsArticle(
    val id: String,
    val title: String,
    val description: String?,
    val source: String,
    val url: String,
    val publishedAt: String,
    val urlToImage: String? = null,
    val category: String = "Technology"
)
