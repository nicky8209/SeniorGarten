package me.melona.seniorgarten.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
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
        val button = binding.button
        button.setOnClickListener {
            val intent = Intent(requireActivity(), CleaningActivity::class.java)
            startActivity(intent)
        }
        val button2 = binding.button2
        button2.setOnClickListener {
            Toast.makeText(requireActivity(), "서비스 제공 준비 중 입니다.", Toast.LENGTH_SHORT).show()
        }
        val button3 = binding.button3
        button3.setOnClickListener {
            Toast.makeText(requireActivity(), "서비스 제공 준비 중 입니다.", Toast.LENGTH_SHORT).show()
        }
        val button4 = binding.button4
        button4.setOnClickListener {
            val intent = Intent(requireActivity(), EmergencyCallActivity::class.java)
            startActivity(intent)
        }
        homeViewModel.text.observe(viewLifecycleOwner) { textView.text = it }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
