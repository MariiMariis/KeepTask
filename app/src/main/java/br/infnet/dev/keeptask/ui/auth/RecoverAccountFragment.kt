package br.infnet.dev.keeptask.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import br.infnet.dev.keeptask.databinding.FragmentRecoverAccountBinding
import br.infnet.dev.keeptask.helper.BaseFragment
import br.infnet.dev.keeptask.helper.FirebaseHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverAccountFragment : BaseFragment() {

    private var _binding: FragmentRecoverAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth

        initClicks()
    }

    private fun initClicks() {
        binding.btnRecover.setOnClickListener { validateData() }}


    private fun validateData() {
        val email = binding.edtEmail.text.toString().trim()

        if(email.isNotEmpty()){

            hideKeyboard()

            binding.progressBar.isVisible = true

            recoverAccountUser(email)

        } else{
            Toast.makeText(requireContext(), "Por favor, informe seu email", Toast.LENGTH_SHORT).show()

        }
    }

    private fun recoverAccountUser(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Link para recuperação de conta enviado ao email informado.", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), FirebaseHelper.validateError(task.exception?.message ?: ""), Toast.LENGTH_SHORT).show()
                }

                binding.progressBar.isVisible = false

            }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}