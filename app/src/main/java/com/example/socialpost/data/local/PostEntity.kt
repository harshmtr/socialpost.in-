package com.example.socialpost.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.socialpost.data.model.PostDraft
import com.example.socialpost.data.model.PostStatus

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val articleTitle: String,
    val articleSource: String,
    val articleUrl: String,
    val content: String,
    val imageUrl: String?,
    val hashtags: String, // stored as comma-separated
    val tone: String,
    val hookStyle: String,
    val status: String,
    val createdAt: Long,
    val isFavorite: Boolean
) {
    fun toPostDraft(): PostDraft {
        return PostDraft(
            id = id,
            articleTitle = articleTitle,
            articleSource = articleSource,
            articleUrl = articleUrl,
            content = content,
            imageUrl = imageUrl,
            hashtags = if (hashtags.isBlank()) emptyList() else hashtags.split(",").map { it.trim() },
            tone = tone,
            hookStyle = hookStyle,
            status = try { PostStatus.valueOf(status) } catch (e: Exception) { PostStatus.DRAFT },
            createdAt = createdAt,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromPostDraft(draft: PostDraft): PostEntity {
            return PostEntity(
                id = draft.id,
                articleTitle = draft.articleTitle,
                articleSource = draft.articleSource,
                articleUrl = draft.articleUrl,
                content = draft.content,
                imageUrl = draft.imageUrl,
                hashtags = draft.hashtags.joinToString(","),
                tone = draft.tone,
                hookStyle = draft.hookStyle,
                status = draft.status.name,
                createdAt = draft.createdAt,
                isFavorite = draft.isFavorite
            )
        }
    }
}
