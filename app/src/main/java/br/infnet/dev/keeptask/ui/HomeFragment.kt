package br.infnet.dev.keeptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.infnet.dev.keeptask.R
import br.infnet.dev.keeptask.databinding.FragmentHomeBinding
import br.infnet.dev.keeptask.databinding.FragmentLoginBinding
import br.infnet.dev.keeptask.ui.adapter.ViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configTabLayout()
    }


    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(TodoFragment(), "A iniciar")
        adapter.addFragment(DoingFragment(), "Em progresso")
        adapter.addFragment(DoneFragment(), "Finalizado")

        binding.viewPager.offscreenPageLimit = adapter.itemCount

        TabLayoutMediator(
            binding.tabs, binding.viewPager
        ) {
            tab, position ->
            tab.text = adapter.getTitle(
                position
            )
        }.attach()
    }

}