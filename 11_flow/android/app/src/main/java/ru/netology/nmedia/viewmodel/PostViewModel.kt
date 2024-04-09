package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.*
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.util.SingleLiveEvent

private val empty = Post(
    id = 0,
    author = "",
    authorAvatar = "",
    authorId = 0,
    content = "",
    published = "",
    likedByMe = false,
    likes = 0,
    hidden = false,
    attachment = null
)


class PostViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(context = application).postDao())

    @OptIn(ExperimentalCoroutinesApi::class)
    val data: LiveData<FeedModel> = AppAuth.getInstance()
        .authState
        .flatMapLatest { auth ->
            repository.data.map{posts ->
                FeedModel(
                    posts.map {it.copy(ownedByMe = auth.id == it.authorId)},
                    posts.isEmpty()
                )
            }
        }
        .asLiveData(Dispatchers.Default)


    val newerCount = repository.data.asLiveData()
        .switchMap {
            repository.getNewerCount(it.firstOrNull()?.id ?: 0L).catch {
                _dataState.postValue(
                    FeedModelState(error = true)
                )
            }.asLiveData(Dispatchers.Default, 100)
        }

    private val _dataState = MutableLiveData<FeedModelState>()
    val dataState: LiveData<FeedModelState>
        get() = _dataState

    private val edited = MutableLiveData(empty)
    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val _photo = MutableLiveData<PhotoModel?>(null)
    val photo: MutableLiveData<PhotoModel?>
        get() = _photo

    init {
        loadPosts()
    }
    fun savePhoto(photoModel: PhotoModel) {
        photo.value = photoModel
    }

    fun clear() {
        photo.value = null
    }


    fun loadPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun refreshPosts() = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(refreshing = true)
            repository.getAll()
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun save() {
        edited.value?.let {
            _postCreated.value = Unit
            viewModelScope.launch {
                try {
                    repository.save(it, _photo.value)
                    _dataState.value = FeedModelState()
                } catch (e: Exception) {
                    _dataState.value = FeedModelState(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            _dataState.value = FeedModelState(loading = true)
            repository.likeById(id)
            _dataState.value = FeedModelState()
        } catch (e: Exception) {
            _dataState.value = FeedModelState(error = true)
        }
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                _dataState.value = FeedModelState(loading = true)
                repository.removeById(id)
                _dataState.value = FeedModelState()
            } catch (e: Exception) {
                _dataState.value = FeedModelState(error = true)
            }
        }

    }

    fun updateHidden() {
        viewModelScope.launch { repository.updateHidden() }
    }
}
