package com.example.socialpost.data.model

data class PostValidationResult(
    val wordCount: Int,
    val hashtagCount: Int,
    val lineBreaks: Int,
    val isValid: Boolean,
    val qualityScore: Int, // 0 to 100
    val suggestions: List<String>
)
