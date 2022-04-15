package br.infnet.dev.keeptask.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import br.infnet.dev.keeptask.R
import br.infnet.dev.keeptask.api.ApiRequests
import br.infnet.dev.keeptask.databinding.FragmentHomeBinding
import br.infnet.dev.keeptask.ui.adapter.ViewPagerAdapter
import com.google.android.gms.ads.MobileAds
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

const val BASE_URL = "https://cat-fact.herokuapp.com/"

class HomeFragment : Fragment() {

    lateinit var mAdView :AdView

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private var TAG = "MainActivity"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        getCurrentData()

        binding.apiLayout.setOnClickListener {
            getCurrentData()
        }

        MobileAds.initialize(this@HomeFragment.requireContext()) {}

        mAdView = binding.adView
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onViewCreated(view, savedInstanceState)

        configTabLayout()

        initClicks()
    }

    private fun initClicks() {

        binding.ibLogout.setOnClickListener { loggoutApp()  }

    }


    private fun getCurrentData() {
        binding.tvText.visibility = View.INVISIBLE
        binding.progbarApi.visibility = View.VISIBLE
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiRequests::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCatFacts().awaitResponse()
                if (response.isSuccessful) {
                    val data = response.body()!!
                    Log.d(TAG, data.text)

                    withContext(Dispatchers.Main) {
                        binding.tvText.visibility = View.VISIBLE
                        binding.progbarApi.visibility = View.GONE

                        binding.tvText.text = data.text
                    }
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(),"Acho que algo deu errado X.X", Toast.LENGTH_SHORT).show()
                }

            }

        }

    }



    private fun loggoutApp(){
        auth.signOut()
        findNavController().navigate(R.id.action_homeFragment_to_authentication)
    }


    private fun configTabLayout() {
        val adapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = adapter

        adapter.addFragment(TodoFragment(), "A Fazer")
        adapter.addFragment(DoingFragment(), "Fazendo")
        adapter.addFragment(DoneFragment(), "Feitas")

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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}