package br.infnet.dev.keeptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import br.infnet.dev.keeptask.R
import br.infnet.dev.keeptask.databinding.FragmentFormTaskBinding
import br.infnet.dev.keeptask.helper.FirebaseHelper
import br.infnet.dev.keeptask.model.Task

class FormTaskFragment : Fragment() {

    private var _binding: FragmentFormTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var task: Task
    private var newTask: Boolean = true

    private var statusTask: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.btnSalvarTask.setOnClickListener{ validateData()}

        binding.radioGroupNewtask.setOnCheckedChangeListener { _, id ->
            statusTask = when(id) {
                R.id.radio_btn_todo -> 0
                R.id.radio_btn_doing -> 1
                else -> 2

            }

        }
    }

    private fun validateData() {
        val description = binding.edtNovaTarefa.text.toString().trim()

        if(description.isNotEmpty()){

            binding.progressBar.isVisible = true

            if(newTask) task = Task()
            task.description = description
            task.status = statusTask

            saveTask()

        } else {
            Toast.makeText(requireContext(), "Informe uma descrição para a tarefa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTask(){
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getUserId() ?: "").child(task.id)
            .setValue(task)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    if(newTask) { //Nova Tarefa
                        Toast.makeText(requireContext(), "Tarefa salva!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        binding.progressBar.isVisible = false
                        Toast.makeText(requireContext(), "Tarefa atualizada com sucesso!.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar tarefa.", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener{
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Erro ao salvar a tarefa", Toast.LENGTH_SHORT).show()


            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}