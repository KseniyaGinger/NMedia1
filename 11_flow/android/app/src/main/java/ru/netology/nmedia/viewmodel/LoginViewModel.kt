package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor (
    private val repository: PostRepository,
    private val appAuth: AppAuth,
) : ViewModel() {

    val _authState = appAuth.authState

    fun authenticate(login: String, password: String) {
        viewModelScope.launch {
            try {
                val result = repository.authenticate(login, password)
                appAuth.setFlow(result)
            } catch (e: NetworkError) {
                appAuth.setFlow(_authState.value.copy(error = e))
            } catch (e: ApiError) {
                appAuth.setFlow(_authState.value.copy(error = e))
            }
        }
    }
}
