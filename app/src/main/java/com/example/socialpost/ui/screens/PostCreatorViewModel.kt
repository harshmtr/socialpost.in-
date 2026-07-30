package com.example.socialpost.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialpost.data.local.AppDatabase
import com.example.socialpost.data.model.NewsArticle
import com.example.socialpost.data.model.PostDraft
import com.example.socialpost.data.model.PostStatus
import com.example.socialpost.data.model.PostValidationResult
import com.example.socialpost.data.repository.AiRepository
import com.example.socialpost.data.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PostCreatorState(
    val article: NewsArticle? = null,
    val postText: String = "",
    val imageUrl: String? = null,
    val hookStyle: String = "Bold Statement",
    val tone: String = "Thought Leadership",
    val includeEmojis: Boolean = true,
    val hashtagCount: Int = 6,
    val customInstruction: String = "",
    val isGeneratingPost: Boolean = false,
    val isGeneratingImage: Boolean = false,
    val isLoadingImages: Boolean = false,
    val imageOptions: List<String> = emptyList(),
    val variations: List<String> = emptyList(),
    val validationResult: PostValidationResult = PostValidationResult(0, 0, 0, false, 0, emptyList()),
    val saveMessage: String? = null,
    val currentSavedPostId: Long? = null
)

class PostCreatorViewModel(application: Application) : AndroidViewModel(application) {

    private val aiRepository = AiRepository()
    private val postRepository: PostRepository

    init {
        val dao = AppDatabase.getInstance(application).postDao()
        postRepository = PostRepository(dao)
    }

    private val _uiState = MutableStateFlow(PostCreatorState())
    val uiState: StateFlow<PostCreatorState> = _uiState.asStateFlow()

    val hookStyles = listOf("Bold Statement", "Thought-Provoking Question", "Surprising Fact", "Data Takeaway")
    val tones = listOf("Thought Leadership", "Professional", "Conversational", "Enthusiastic")

    fun setArticle(article: NewsArticle) {
        val defaultImage = article.urlToImage ?: aiRepository.generateAiImageUrl(article)
        _uiState.value = _uiState.value.copy(
            article = article,
            imageUrl = defaultImage,
            currentSavedPostId = null,
            saveMessage = null
        )
        generateDraft()
    }

    fun updatePostText(newText: String) {
        val validation = aiRepository.validatePost(newText)
        _uiState.value = _uiState.value.copy(
            postText = newText,
            validationResult = validation
        )
    }

    fun updateHookStyle(hook: String) {
        _uiState.value = _uiState.value.copy(hookStyle = hook)
    }

    fun updateTone(tone: String) {
        _uiState.value = _uiState.value.copy(tone = tone)
    }

    fun updateIncludeEmojis(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(includeEmojis = enabled)
    }

    fun updateHashtagCount(count: Int) {
        _uiState.value = _uiState.value.copy(hashtagCount = count)
    }

    fun updateCustomInstruction(instruction: String) {
        _uiState.value = _uiState.value.copy(customInstruction = instruction)
    }

    fun generateDraft() {
        val state = _uiState.value
        val currentArticle = state.article ?: NewsArticle(
            id = "custom_topic",
            title = "Trending Innovation in Technology",
            description = "AI and modern cloud software are reshaping digital transformation globally.",
            source = "Tech Industry",
            url = "https://techcrunch.com",
            publishedAt = "Today"
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPost = true, saveMessage = null)
            try {
                val post = aiRepository.generateLinkedInPost(
                    article = currentArticle,
                    hookStyle = state.hookStyle,
                    tone = state.tone,
                    includeEmojis = state.includeEmojis,
                    hashtagCount = state.hashtagCount,
                    customInstruction = state.customInstruction
                )
                val validation = aiRepository.validatePost(post)
                _uiState.value = _uiState.value.copy(
                    postText = post,
                    validationResult = validation,
                    isGeneratingPost = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingPost = false)
            }
        }
    }

    fun generateVariations() {
        val currentArticle = _uiState.value.article ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingPost = true)
            try {
                val list = aiRepository.generatePostVariations(
                    article = currentArticle,
                    count = 3,
                    tone = _uiState.value.tone
                )
                _uiState.value = _uiState.value.copy(
                    variations = list,
                    isGeneratingPost = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isGeneratingPost = false)
            }
        }
    }

    fun generateNewAiImage() {
        val currentArticle = _uiState.value.article ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGeneratingImage = true,
                isLoadingImages = true
            )
            val options = aiRepository.generateImageOptions(currentArticle)
            _uiState.value = _uiState.value.copy(
                imageOptions = options,
                isGeneratingImage = false,
                isLoadingImages = false
            )
        }
    }

    fun selectImage(url: String) {
        _uiState.value = _uiState.value.copy(
            imageUrl = url,
            imageOptions = emptyList()
        )
    }

    fun closeImagePicker() {
        _uiState.value = _uiState.value.copy(imageOptions = emptyList())
    }

    fun saveDraft(status: PostStatus = PostStatus.DRAFT) {
        val state = _uiState.value
        val article = state.article
        if (state.postText.isBlank()) return

        viewModelScope.launch {
            val hashtags = aiRepository.extractHashtags(state.postText)
            val draft = PostDraft(
                id = state.currentSavedPostId ?: 0,
                articleTitle = article?.title ?: "Custom Tech Post",
                articleSource = article?.source ?: "SocialPost AI",
                articleUrl = article?.url ?: "",
                content = state.postText,
                imageUrl = state.imageUrl,
                hashtags = hashtags,
                tone = state.tone,
                hookStyle = state.hookStyle,
                status = status
            )

            val savedId = postRepository.savePost(draft)
            _uiState.value = _uiState.value.copy(
                currentSavedPostId = savedId,
                saveMessage = if (status == PostStatus.PUBLISHED) "Post published and saved!" else "Draft saved to database!"
            )
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(saveMessage = null)
    }
}
