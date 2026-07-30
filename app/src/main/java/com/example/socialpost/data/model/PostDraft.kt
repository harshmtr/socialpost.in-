package com.example.socialpost.data.model

data class PostDraft(
    val id: Long = 0,
    val articleTitle: String,
    val articleSource: String,
    val articleUrl: String,
    val content: String,
    val imageUrl: String? = null,
    val hashtags: List<String> = emptyList(),
    val tone: String = "Professional",
    val hookStyle: String = "Bold Statement",
    val status: PostStatus = PostStatus.DRAFT,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

enum class PostStatus {
    DRAFT,
    PUBLISHED,
    SCHEDULED
}
