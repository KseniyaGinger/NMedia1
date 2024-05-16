package ru.netology.nmedia.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.Media
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.PhotoModel

interface PostRepository {

    val data: Flow<PagingData<Post>>
    suspend fun getAll()
    fun getNewerCount(id: Long): Flow<Int>
    suspend fun save(post: Post, photo: PhotoModel?)
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long)

    suspend fun updateHidden()

    suspend fun authenticate(login: String, password: String) : AuthState
}

