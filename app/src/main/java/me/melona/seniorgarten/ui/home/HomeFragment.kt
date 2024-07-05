package me.melona.seniorgarten.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import me.melona.seniorgarten.CleaningActivity
import me.melona.seniorgarten.Constants
import me.melona.seniorgarten.EmergencyCallActivity
import me.melona.seniorgarten.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        if (Constants.IS_DEBUG) {
            textView.visibility = View.VISIBLE
        }
        val button: Button = binding.button
        val button2: Button = binding.button2
        val button3: Button = binding.button3
        val button4: Button = binding.button4

        button2.text = "반찬배달"
        button2.isEnabled = false
        button3.text = "병원동행"
        button3.isEnabled = false
        homeViewModel.text.observe(viewLifecycleOwner) { textView.text = it }
        button.setOnClickListener {
            val intent = Intent(requireActivity(), CleaningActivity::class.java)
            startActivity(intent)
        }
        button4.setOnClickListener {
            val intent = Intent(requireActivity(), EmergencyCallActivity::class.java)
            startActivity(intent)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
