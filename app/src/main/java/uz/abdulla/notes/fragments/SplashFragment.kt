package uz.abdulla.notes.fragments

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import uz.abdulla.notes.R
import uz.abdulla.notes.databinding.FragmentSplashBinding

class SplashFragment : Fragment(R.layout.fragment_splash) {
    private var _binding:FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        },2500)



    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
    

}