package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.viewmodel.LoginViewModel


class AuthFragment : Fragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bindingAuth = FragmentAuthBinding.inflate(inflater, container, false)

        bindingAuth.signButton.setOnClickListener {
            val log = bindingAuth.login.editText?.text.toString()
            val pas = bindingAuth.password.editText?.text.toString()

            viewModel.authenticate(log,pas)
        }

        lifecycleScope.launch {

            viewModel._authState.collect { authState ->
                if (authState.error != null) {
                    Toast.makeText(bindingAuth.root.context, "Отсутствует подключение к интернету", Toast.LENGTH_SHORT)
                        .show()
                } else if (authState.id != 0L) {
                    findNavController().navigateUp()
                }
            }
        }
        return bindingAuth.root
    }
}

