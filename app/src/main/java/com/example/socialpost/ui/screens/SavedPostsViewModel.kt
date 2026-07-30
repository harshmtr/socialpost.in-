package com.example.socialpost.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialpost.data.local.AppDatabase
import com.example.socialpost.data.model.PostDraft
import com.example.socialpost.data.model.PostStatus
import com.example.socialpost.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class PostFilter {
    ALL,
    DRAFTS,
    PUBLISHED,
    FAVORITES
}

class SavedPostsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PostRepository

    init {
        val dao = AppDatabase.getInstance(application).postDao()
        repository = PostRepository(dao)
    }

    private val _currentFilter = MutableStateFlow(PostFilter.ALL)
    val currentFilter: StateFlow<PostFilter> = _currentFilter

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val posts: StateFlow<List<PostDraft>> = combine(
        repository.allPosts,
        _currentFilter,
        _searchQuery
    ) { all, filter, query ->
        all.filter { post ->
            val matchesFilter = when (filter) {
                PostFilter.ALL -> true
                PostFilter.DRAFTS -> post.status == PostStatus.DRAFT
                PostFilter.PUBLISHED -> post.status == PostStatus.PUBLISHED
                PostFilter.FAVORITES -> post.isFavorite
            }
            val matchesQuery = query.isBlank() ||
                    post.content.contains(query, ignoreCase = true) ||
                    post.articleTitle.contains(query, ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(filter: PostFilter) {
        _currentFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(post: PostDraft) {
        viewModelScope.launch {
            repository.toggleFavorite(post.id, post.isFavorite)
        }
    }

    fun deletePost(post: PostDraft) {
        viewModelScope.launch {
            repository.deletePost(post.id)
        }
    }

    fun markAsPublished(post: PostDraft) {
        viewModelScope.launch {
            repository.updateStatus(post.id, PostStatus.PUBLISHED)
        }
    }
}
