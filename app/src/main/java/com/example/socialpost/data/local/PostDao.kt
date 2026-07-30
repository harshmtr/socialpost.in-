package com.example.socialpost.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY createdAt DESC")
    fun getAllPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE status = :status ORDER BY createdAt DESC")
    fun getPostsByStatus(status: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoritePosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getPostById(id: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity): Long

    @Update
    suspend fun updatePost(post: PostEntity)

    @Query("DELETE FROM posts WHERE id = :id")
    suspend fun deletePostById(id: Long)

    @Query("UPDATE posts SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("UPDATE posts SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)
}
