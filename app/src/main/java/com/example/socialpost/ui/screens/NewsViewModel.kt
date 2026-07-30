package com.example.socialpost.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialpost.data.model.NewsArticle
import com.example.socialpost.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(
        val articles: List<NewsArticle>,
        val selectedCategory: String,
        val searchQuery: String
    ) : NewsUiState
    data class Error(val message: String) : NewsUiState
}

class NewsViewModel(
    private val newsRepository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private var currentCategory = "Technology"
    private var currentQuery = ""

    val categories = listOf("Technology", "AI & ML", "Mobile Tech", "Cloud & DevOps", "Quantum", "Cybersecurity", "Startups")

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            try {
                val articles = if (currentQuery.isNotBlank()) {
                    newsRepository.searchNews(currentQuery)
                } else {
                    newsRepository.getTopTechNews(currentCategory)
                }
                _uiState.value = NewsUiState.Success(
                    articles = articles,
                    selectedCategory = currentCategory,
                    searchQuery = currentQuery
                )
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error(e.localizedMessage ?: "Failed to load tech news")
            }
        }
    }

    fun selectCategory(category: String) {
        if (currentCategory != category) {
            currentCategory = category
            currentQuery = ""
            loadNews()
        }
    }

    fun searchNews(query: String) {
        currentQuery = query
        loadNews()
    }
}
