package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val appAuth: AppAuth
): ViewModel() {
    val data = appAuth.authState

    val authenticated: Boolean
        get() = data.value.id != 0L
}