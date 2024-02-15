 package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity

@Dao interface PostDao {
   @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT COUNT(*) FROM PostEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM PostEntity WHERE hidden = 0 ORDER BY id DESC")
    fun getAllVisible(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) FROM PostEntity WHERE hidden = 1")
    suspend fun getAllHidden(): Int

    @Query("UPDATE PostEntity SET hidden = 0 WHERE hidden = 1")
    suspend fun updateHidden(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Long)
}


