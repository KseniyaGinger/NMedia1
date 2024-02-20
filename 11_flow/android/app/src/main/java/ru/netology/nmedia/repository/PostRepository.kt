package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Flow<List<PostEntity>>>
    suspend fun save(post: Post, photo: PhotoModel?)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)

    suspend fun updateHidden()


}

