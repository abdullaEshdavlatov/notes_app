package uz.abdulla.notes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import uz.abdulla.notes.BuildConfig
import uz.abdulla.notes.R
import uz.abdulla.notes.databinding.FragmentAboutNoteBinding

class AboutNoteFragment : Fragment(R.layout.fragment_about_note) {

    private var _binding: FragmentAboutNoteBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAboutNoteBinding.bind(view)

        binding.appVersion.setText(R.string.version)


        val textVersion = resources.getString(R.string.version)

        binding.appVersion.text = "${BuildConfig.VERSION_NAME} $textVersion"
        binding.appDescription.setText(R.string.app_info)

        binding.icBack.setOnClickListener{
            findNavController().navigate(R.id.action_aboutNoteFragment_to_mainFragment)
        }

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}