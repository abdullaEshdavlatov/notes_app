package uz.abdulla.notes.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import uz.abdulla.notes.MainActivity
import uz.abdulla.notes.R
import uz.abdulla.notes.databinding.BottomSheetDialogBinding
import uz.abdulla.notes.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: Editor

    private lateinit var dialog:BottomSheetDialog
    private var language = "English"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        sharedPreferences = activity?.applicationContext!!.getSharedPreferences("SHARED_PREF",Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        language = sharedPreferences.getString("language","English")!!

        binding.language.setOnClickListener {
            val dialogBinding = BottomSheetDialogBinding.inflate(layoutInflater)
            dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
            dialog.setContentView(dialogBinding.root)

            dialogBinding.layoutEnglish.setOnClickListener {
                dialogBinding.layoutEnglish.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                dialogBinding.tvEnglish.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                editor.putString("language","English")
                editor.apply()
                language = "English"
                dialog.dismiss()
                requireActivity().recreate()
            }

            dialogBinding.layoutUzbek.setOnClickListener {
                dialogBinding.layoutUzbek.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                dialogBinding.tvUzbek.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                editor.putString("language","Uzbek")
                editor.apply()
                language = "Uzbek"
                dialog.dismiss()
                requireActivity().recreate()
            }
            dialogBinding.layoutRussian.setOnClickListener {
                dialogBinding.layoutRussian.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                dialogBinding.tvRussian.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                editor.putString("language","Russia")
                editor.apply()
                language = "Russia"
                dialog.dismiss()
                requireActivity().recreate()
            }

            when(language){
                "English" ->{
                    dialogBinding.layoutEnglish.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                    dialogBinding.tvEnglish.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                }
                "Uzbek" ->{
                    dialogBinding.layoutUzbek.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                    dialogBinding.tvUzbek.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                }
                "Russia" ->{
                    dialogBinding.layoutRussian.background = ContextCompat.getDrawable(requireContext(),R.drawable.language_background)
                    dialogBinding.tvRussian.setTextColor(ContextCompat.getColor(requireContext(),R.color.violet))
                }
            }

            dialog.show()
        }
        binding.icBack.setOnClickListener{
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}