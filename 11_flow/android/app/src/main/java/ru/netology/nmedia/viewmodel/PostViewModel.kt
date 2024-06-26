package ru.netology.nmedia.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.model.FeedModelState
import ru.netology.nmedia.model.PhotoModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.util.SingleLiveEvent
import javax.inject.Inject

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


@HiltViewModel
class PostViewModel @Inject constructor (
    private val repository: PostRepository,
   appAuth: AppAuth,
) : ViewModel() {

    private var maxId = 0L


    @OptIn(ExperimentalCoroutinesApi::class)
    val data: Flow<PagingData<Post>> = appAuth
        .authState
        .flatMapLatest { (auth, _) ->
            maxId = 0
            repository.data.map{posts ->
                    posts.map {
                        maxId = maxOf(maxId, it.id)
                        it.copy(ownedByMe = it.authorId == auth)}
                        .filter { !it.hidden }
            }
        }
        .flowOn(Dispatchers.Default)




    @OptIn(ExperimentalCoroutinesApi::class)
   val newerCount = repository.getNewerCount(maxId)

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
