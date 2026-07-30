package com.example.socialpost.data.repository

import com.example.socialpost.BuildConfig
import com.example.socialpost.data.model.NewsArticle
import com.example.socialpost.data.remote.NewsApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NewsRepository {

    private val apiService: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.currentsapi.services/v1/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    suspend fun getTopTechNews(category: String = "Technology"): List<NewsArticle> {
        val apiKey = BuildConfig.NEWS_API_KEY
        if (apiKey.isNotBlank()) {
            try {
                println("NewsRepository: Fetching live tech news from Currents API...")
                // Currents API categories might differ, we'll pass it if it's not the default "Technology" mapping
                val currentsCategory = when(category.lowercase()) {
                    "technology" -> "technology"
                    "ai & ml" -> "science"
                    "mobile tech" -> "technology"
                    else -> null
                }
                
                val response = apiService.getTopHeadlines(
                    apiKey = apiKey,
                    category = currentsCategory
                )
                
                if (response.status == "ok" && !response.news.isNullOrEmpty()) {
                    println("NewsRepository: Successfully fetched ${response.news.size} live articles.")
                    return response.news.map { article ->
                        NewsArticle(
                            id = article.id ?: "news_${article.hashCode()}",
                            title = article.title ?: "Untitled Article",
                            description = article.description,
                            source = article.author ?: "Tech News",
                            url = article.url ?: "https://news.google.com",
                            publishedAt = article.published ?: "Recently",
                            urlToImage = article.image,
                            category = article.category?.firstOrNull() ?: category
                        )
                    }
                } else {
                    println("NewsRepository: Currents API returned status ${response.status} or no news.")
                }
            } catch (e: Exception) {
                println("NewsRepository: Currents API call failed: ${e.message}")
                e.printStackTrace()
            }
        } else {
            println("NewsRepository: No Currents API key found. Using curated fallback.")
        }
        return getCuratedTechNews(category)
    }

    suspend fun searchNews(query: String): List<NewsArticle> {
        val apiKey = BuildConfig.NEWS_API_KEY
        if (apiKey.isNotBlank() && query.isNotBlank()) {
            try {
                println("NewsRepository: Searching live news via Currents API for: $query")
                val response = apiService.searchNews(apiKey = apiKey, query = query)
                if (response.status == "ok" && !response.news.isNullOrEmpty()) {
                    println("NewsRepository: Found ${response.news.size} results for query: $query")
                    return response.news.map { article ->
                        NewsArticle(
                            id = article.id ?: "search_${article.hashCode()}",
                            title = article.title ?: "Untitled Search Result",
                            description = article.description,
                            source = article.author ?: "News Source",
                            url = article.url ?: "https://news.google.com",
                            publishedAt = article.published ?: "Recently",
                            urlToImage = article.image,
                            category = "Search"
                        )
                    }
                }
            } catch (e: Exception) {
                println("NewsRepository: Search failed: ${e.message}")
            }
        }

        val allCurated = getCuratedTechNews("All")
        return if (query.isBlank()) allCurated else allCurated.filter {
            it.title.contains(query, ignoreCase = true) ||
                    (it.description?.contains(query, ignoreCase = true) == true)
        }
    }

    private fun getCuratedTechNews(categoryFilter: String): List<NewsArticle> {
        val curatedList = listOf(
            NewsArticle(
                id = "curated_1",
                title = "Gemini 3.5 & Next-Gen AI Models Revolutionize Developer Productivity",
                description = "Google and leading AI research labs unveil new multimodal model architectures boasting 2 million token context windows, agentic reasoning capabilities, and lightning-fast inference for mobile edge applications.",
                source = "TechCrunch",
                url = "https://techcrunch.com/artificial-intelligence",
                publishedAt = "2026-07-28T10:30:00Z",
                urlToImage = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=800&auto=format&fit=crop",
                category = "AI & ML"
            ),
            NewsArticle(
                id = "curated_2",
                title = "Android 16 Unveils Seamless Multi-Device Continuity & Spatial Audio APIs",
                description = "Google releases the flagship SDK for Android 16, introducing native edge-AI acceleration, enhanced privacy sandbox protocols, and unified foldable windowing APIs.",
                source = "Android Developers Blog",
                url = "https://developer.android.com/news",
                publishedAt = "2026-07-27T14:15:00Z",
                urlToImage = "https://images.unsplash.com/photo-1607252650355-f7fd0460ccdb?w=800&auto=format&fit=crop",
                category = "Mobile Tech"
            ),
            NewsArticle(
                id = "curated_3",
                title = "Cloud Native Computing Foundation Reports 85% Enterprise Adoption of Serverless AI Workloads",
                description = "Modern devops teams are transitioning from traditional Kubernetes clusters to serverless GPU infrastructure for real-time model inference and scalable microservices.",
                source = "VentureBeat",
                url = "https://venturebeat.com/cloud",
                publishedAt = "2026-07-26T09:00:00Z",
                urlToImage = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop",
                category = "Cloud & DevOps"
            ),
            NewsArticle(
                id = "curated_4",
                title = "Quantum Supremacy Milestone Achieved in Fault-Tolerant Error Correction",
                description = "Researchers demonstrate a 10,000 logical qubit quantum processor capable of running complex molecular chemistry simulations in minutes rather than millennia.",
                source = "MIT Technology Review",
                url = "https://technologyreview.com/quantum",
                publishedAt = "2026-07-25T16:45:00Z",
                urlToImage = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=800&auto=format&fit=crop",
                category = "Quantum"
            ),
            NewsArticle(
                id = "curated_5",
                title = "Zero-Trust Security Standard Mandatory for All Enterprise Cloud APIs",
                description = "Global cybersecurity regulators mandate cryptographic hardware keys and post-quantum token verification across all enterprise SaaS integrations.",
                source = "Wired",
                url = "https://wired.com/story/cybersecurity-standards",
                publishedAt = "2026-07-24T11:20:00Z",
                urlToImage = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=800&auto=format&fit=crop",
                category = "Cybersecurity"
            ),
            NewsArticle(
                id = "curated_6",
                title = "Venture Capital Funding Surges $42B into Autonomous Robotics & AI Hardware",
                description = "Q3 investment reports confirm record capital deployment into humanoid robotics, spatial computing sensors, and custom silicon chip startups.",
                source = "Forbes Tech",
                url = "https://forbes.com/innovation",
                publishedAt = "2026-07-23T13:00:00Z",
                urlToImage = "https://images.unsplash.com/photo-1485827404703-89b55fcc595e?w=800&auto=format&fit=crop",
                category = "Startups"
            )
        )

        return if (categoryFilter == "All" || categoryFilter == "Technology") {
            curatedList
        } else {
            curatedList.filter { it.category.equals(categoryFilter, ignoreCase = true) }
        }
    }
}
