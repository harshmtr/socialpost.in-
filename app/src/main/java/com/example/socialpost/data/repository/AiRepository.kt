package com.example.socialpost.data.repository

import com.example.socialpost.BuildConfig
import com.example.socialpost.data.model.NewsArticle
import com.example.socialpost.data.model.PostValidationResult
import com.example.socialpost.data.remote.GeminiClient
import com.example.socialpost.data.remote.GeminiContent
import com.example.socialpost.data.remote.GeminiGenerateRequest
import com.example.socialpost.data.remote.GeminiGenerationConfig
import com.example.socialpost.data.remote.GeminiPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import kotlin.random.Random

class AiRepository {

    suspend fun generateLinkedInPost(
        article: NewsArticle,
        hookStyle: String = "Bold Statement",
        tone: String = "Professional",
        includeEmojis: Boolean = true,
        hashtagCount: Int = 6,
        customInstruction: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        val prompt = buildString {
            append("Write an engaging, high-performing LinkedIn post based on this tech news:\n\n")
            append("TITLE: ${article.title}\n")
            append("SOURCE: ${article.source}\n")
            append("DESCRIPTION: ${article.description ?: "N/A"}\n\n")
            append("POST REQUIREMENTS:\n")
            append("1. Hook Style: $hookStyle (Make the first line grab attention immediately)\n")
            append("2. Tone: $tone (Keep it authentic and professional)\n")
            append("3. Key Highlights: Extract 3 bullet points with insights/takeaways\n")
            if (includeEmojis) append("4. Include relevant emojis (e.g. 💡🚀📊🌐🤖)\n")
            append("5. Hashtags: Include $hashtagCount relevant, high-traffic tech hashtags at the end\n")
            append("6. Call to Action: End with a compelling question to drive comments\n")
            append("7. Format: Clean short paragraphs with double line breaks for readability\n")
            if (customInstruction.isNotBlank()) {
                append("8. Additional instruction: $customInstruction\n")
            }
        }

        if (apiKey.isNotBlank()) {
            try {
                println("AiRepository: Attempting live Gemini post generation...")
                val systemInstruction = GeminiContent(
                    parts = listOf(
                        GeminiPart(
                            text = "You are an expert LinkedIn ghostwriter for tech executives. You write compelling, viral LinkedIn posts that maintain professional credibility while maximizing engagement."
                        )
                    )
                )
                val request = GeminiGenerateRequest(
                    contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                    systemInstruction = systemInstruction,
                    generationConfig = GeminiGenerationConfig(temperature = 0.7f)
                )

                val response = GeminiClient.api.generateContent(apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!text.isNullOrBlank()) {
                    println("AiRepository: Live post generation successful.")
                    return@withContext text.trim()
                } else {
                    println("AiRepository: Gemini API returned empty content. Candidates: ${response.candidates?.size ?: 0}")
                }
            } catch (e: Exception) {
                println("AiRepository: Gemini API call failed: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("AiRepository: No Gemini API key found. Using offline fallback.")
        }

        // Offline / Fallback Post Generator
        generateFallbackPost(article, hookStyle, tone, includeEmojis, hashtagCount)
    }

    suspend fun generatePostVariations(
        article: NewsArticle,
        count: Int = 3,
        tone: String = "Professional"
    ): List<String> = withContext(Dispatchers.IO) {
        val hooks = listOf("Bold Statement", "Thought-Provoking Question", "Data-Driven Takeaway")
        val result = mutableListOf<String>()

        for (i in 0 until count) {
            val hook = hooks[i % hooks.size]
            val post = generateLinkedInPost(
                article = article,
                hookStyle = hook,
                tone = tone,
                includeEmojis = true,
                hashtagCount = 5 + i
            )
            result.add(post)
        }
        result
    }

    fun generateAiImageUrl(article: NewsArticle): String {
        val themeKeywords = extractVisualThemeKeywords(article)
        val prompt = "Professional modern LinkedIn tech post header banner depicting $themeKeywords, blue and purple dark corporate tech theme, clean vector art illustration, high resolution, minimalist"
        return buildPollinationsUrl(prompt)
    }

    fun generateImageOptions(article: NewsArticle): List<String> {
        val themeKeywords = extractVisualThemeKeywords(article)
        
        return listOf(
            // Option 1: Professional Vector (Modern Business Style)
            buildPollinationsUrl("Professional LinkedIn banner for $themeKeywords, clean vector art illustration, corporate blue and white theme, minimalist business style, high quality"),
            
            // Option 2: 3D Tech (Futuristic Style)
            buildPollinationsUrl("Futuristic 3D high-tech render of $themeKeywords, glowing neon accents, deep purple and teal lighting, cinematic 8k, unreal engine 5 aesthetic"),
            
            // Option 3: Abstract Digital (Data Style)
            buildPollinationsUrl("Abstract digital data visualization of $themeKeywords, connectivity network lines, dark professional aesthetic, geometric patterns, sleek technology wallpaper"),
            
            // Option 4: Realistic Workspace (Professional Environment)
            buildPollinationsUrl("Modern minimalist tech workspace with a laptop showing $themeKeywords concepts, high-end office photography, soft natural lighting, professional depth of field"),
            
            // Option 5: Creative Illustration (Artistic Style)
            buildPollinationsUrl("Creative flat design illustration depicting $themeKeywords, vibrant modern colors, startup aesthetic, clean lines, professional digital art")
        )
    }

    private fun buildPollinationsUrl(prompt: String): String {
        val encodedPrompt = URLEncoder.encode(prompt, "UTF-8")
        val seed = Random.nextInt(10000, 99999)
        return "https://image.pollinations.ai/prompt/$encodedPrompt?width=1024&height=600&seed=$seed&nologo=true&model=flux"
    }

    private fun extractVisualThemeKeywords(article: NewsArticle): String {
        val text = "${article.title} ${article.description ?: ""}".lowercase()
        return when {
            text.contains("ai") || text.contains("gemini") || text.contains("model") -> "artificial intelligence neural network digital brain"
            text.contains("android") || text.contains("mobile") -> "modern mobile technology smartphone UI network"
            text.contains("cloud") || text.contains("devops") -> "cloud server network database infrastructure"
            text.contains("quantum") -> "quantum computing qubit particle wave physics"
            text.contains("security") || text.contains("zero-trust") -> "cybersecurity digital shield encryption code"
            text.contains("robot") || text.contains("autonomous") -> "futuristic robotics automation mechanical AI"
            else -> "modern technology network digital transformation abstract data"
        }
    }

    fun extractHashtags(content: String): List<String> {
        val hashtagRegex = Regex("#[A-Za-z0-9_]+")
        return hashtagRegex.findAll(content).map { it.value }.toList()
    }

    fun validatePost(content: String): PostValidationResult {
        val trimmed = content.trim()
        val words = if (trimmed.isBlank()) 0 else trimmed.split(Regex("\\s+")).size
        val hashtags = extractHashtags(content)
        val lineBreaks = content.count { it == '\n' }

        val suggestions = mutableListOf<String>()
        var score = 100

        if (words < 80) {
            suggestions.add("Add more key takeaways (aim for 120-250 words for optimal reach).")
            score -= 20
        } else if (words > 350) {
            suggestions.add("Post might be too long. Trim slightly to keep readers engaged.")
            score -= 15
        }

        if (hashtags.size < 3) {
            suggestions.add("Add 3-6 relevant hashtags at the bottom to increase discoverability.")
            score -= 15
        } else if (hashtags.size > 8) {
            suggestions.add("Too many hashtags can look spammy. Reduce to 4-7 hashtags.")
            score -= 10
        }

        if (lineBreaks < 3) {
            suggestions.add("Use more line breaks to create white space and improve mobile readability.")
            score -= 20
        }

        if (!content.contains("?") && !content.lowercase().contains("what do you think")) {
            suggestions.add("End with a call to action or question to drive comments in the algorithm.")
            score -= 10
        }

        return PostValidationResult(
            wordCount = words,
            hashtagCount = hashtags.size,
            lineBreaks = lineBreaks,
            isValid = words in 50..400 && hashtags.size in 2..10,
            qualityScore = score.coerceIn(10, 100),
            suggestions = suggestions
        )
    }

    private fun generateFallbackPost(
        article: NewsArticle,
        hookStyle: String,
        tone: String,
        includeEmojis: Boolean,
        hashtagCount: Int
    ): String {
        val emojiPrefix = if (includeEmojis) "🚀 " else ""
        val bulbEmoji = if (includeEmojis) "💡 " else ""
        val pointEmoji = if (includeEmojis) "🔹 " else ""

        val hook = when (hookStyle) {
            "Thought-Provoking Question" -> "Are we witnessing the biggest inflection point in ${article.category.lowercase()} this decade?"
            "Surprising Fact" -> "Big news in tech today: ${article.title}"
            else -> "The landscape of ${article.category.lowercase()} just shifted permanently."
        }

        val hashtags = listOf(
            "#Technology", "#Innovation", "#TechNews", "#AI",
            "#SoftwareEngineering", "#FutureOfTech", "#Leadership", "#DigitalTransformation"
        ).take(hashtagCount).joinToString(" ")

        return buildString {
            append("$emojiPrefix$hook\n\n")
            append("${article.source} recently highlighted a major update:\n")
            append("${article.title}\n\n")
            append("${bulbEmoji}3 Key Takeaways for Tech Leaders:\n\n")
            append("${pointEmoji}Major Advance: ${article.description ?: "Industry standards are evolving rapidly with new technology capabilities."}\n\n")
            append("${pointEmoji}Strategic Impact: Organizations adopting these tools early gain a significant speed advantage.\n\n")
            append("${pointEmoji}Future Outlook: Expect broader integration across enterprise workflows in the coming quarters.\n\n")
            append("What's your take on this development? Will this change your team's roadmap?\n\n")
            append("👇 Let me know in the comments!\n\n")
            append(hashtags)
        }
    }
}
