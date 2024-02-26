package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.databinding.FragmentPictureBinding
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.view.load
import ru.netology.nmedia.viewmodel.PostViewModel

class FragmentPicture : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private lateinit var binding: FragmentPictureBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentPictureBinding.inflate(layoutInflater)

        val url = arguments?.textArg ?: ""

            Glide.with(this)
                .load(url)
                .into(binding.picture)

        binding.picture.load("${BuildConfig.BASE_URL}/media/${url}")

        binding.fabCancel.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentPicture_to_feedFragment)
        }

        return binding.root
    }
}