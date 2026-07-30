package com.example.socialpost.data.repository

import com.example.socialpost.data.local.PostDao
import com.example.socialpost.data.local.PostEntity
import com.example.socialpost.data.model.PostDraft
import com.example.socialpost.data.model.PostStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PostRepository(private val postDao: PostDao) {

    val allPosts: Flow<List<PostDraft>> = postDao.getAllPosts().map { list ->
        list.map { it.toPostDraft() }
    }

    val favoritePosts: Flow<List<PostDraft>> = postDao.getFavoritePosts().map { list ->
        list.map { it.toPostDraft() }
    }

    fun getPostsByStatus(status: PostStatus): Flow<List<PostDraft>> {
        return postDao.getPostsByStatus(status.name).map { list ->
            list.map { it.toPostDraft() }
        }
    }

    suspend fun savePost(draft: PostDraft): Long {
        return postDao.insertPost(PostEntity.fromPostDraft(draft))
    }

    suspend fun updatePost(draft: PostDraft) {
        postDao.updatePost(PostEntity.fromPostDraft(draft))
    }

    suspend fun deletePost(id: Long) {
        postDao.deletePostById(id)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        postDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun updateStatus(id: Long, status: PostStatus) {
        postDao.updateStatus(id, status.name)
    }
}
