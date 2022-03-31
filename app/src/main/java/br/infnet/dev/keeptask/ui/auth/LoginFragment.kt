package br.infnet.dev.keeptask.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.infnet.dev.keeptask.R
import br.infnet.dev.keeptask.databinding.FragmentLoginBinding
import br.infnet.dev.keeptask.helper.BaseFragment
import br.infnet.dev.keeptask.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnLogin.setOnClickListener {
            validateData()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }

        binding.btnRecover.setOnClickListener {
            findNavController().navigate(R.id.recoverAccountFragment)

        }
    }


    private fun validateData() {
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString()

        if(email.isNotEmpty()){
            if(password.isNotEmpty()){

                hideKeyboard()

                binding.progressBar.isVisible = true

                loginUser(email, password)

            } else{
                Toast.makeText(requireContext(), "Por favor, insira uma senha", Toast.LENGTH_SHORT).show()

            }
        } else{
            Toast.makeText(requireContext(), "Por favor, informe seu email", Toast.LENGTH_SHORT).show()

        }

    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    findNavController().navigate(R.id.action_global_homeFragment)
                } else {
                    Toast.makeText(requireContext(), FirebaseHelper.validateError(task.exception?.message ?: ""), Toast.LENGTH_SHORT).show()
                    binding.progressBar.isVisible = false

                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}