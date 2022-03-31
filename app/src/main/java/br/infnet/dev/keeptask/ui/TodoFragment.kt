package br.infnet.dev.keeptask.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.infnet.dev.keeptask.R
import br.infnet.dev.keeptask.databinding.FragmentTodoBinding
import br.infnet.dev.keeptask.helper.FirebaseHelper
import br.infnet.dev.keeptask.model.Task
import br.infnet.dev.keeptask.ui.adapter.TaskAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TodoFragment : Fragment() {



    private var _binding: FragmentTodoBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTodoBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClicks()
        getTask()
    }


    private fun initClicks() {
        binding.fabAdd.setOnClickListener{
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)
            findNavController().navigate(action)
        }
    }

    private fun getTask() {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getUserId() ?: "")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {

                        taskList.clear()

                        for (snap in snapshot.children) {
                            val task = snap.getValue(Task::class.java) as Task

                            if(task.status == 0) taskList.add(task)
                        }

                        taskList.reverse()
                        initAdapter()
                    }

                    emptyTasks()
                    binding.progressBar.isVisible = false
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Erro", Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun emptyTasks() {
        binding.textInfo.text = if(taskList.isEmpty()){
            getText(R.string.no_task_registered_todo)
        }else{
            ""
        }
    }

    private fun initAdapter() {
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTask.setHasFixedSize(true)
        taskAdapter = TaskAdapter(requireContext(), taskList) { task, select ->
            optionSelected(task, select)
        }
        binding.rvTask.adapter = taskAdapter
    }

    private fun optionSelected(task:Task, select: Int) {
        when(select) {
            TaskAdapter.SELECT_REMOVE -> {
                deleteTask(task)
            }
            TaskAdapter.SELECT_EDIT -> {
               val action = HomeFragmentDirections
                   .actionHomeFragmentToFormTaskFragment(task)
                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_NEXT -> {
                task.status = 1
                updateTask(task)
            }
        }
    }

    private fun updateTask(task: Task){
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getUserId() ?: "")
            .child(task.id)
            .setValue(task)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    binding.progressBar.isVisible = false
                    Toast.makeText(
                        requireContext(),
                        "Tarefa atualizada com sucesso!.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar tarefa.", Toast.LENGTH_SHORT)
                        .show()
                }

            }.addOnFailureListener{
                binding.progressBar.isVisible = false
                Toast.makeText(requireContext(), "Erro ao salvar a tarefa", Toast.LENGTH_SHORT).show()


            }
    }

    private fun deleteTask(task:Task) {
        FirebaseHelper
            .getDatabase()
            .child("task")
            .child(FirebaseHelper.getUserId() ?: "")
            .child(task.id)
            .removeValue()

        taskList.remove(task)
        taskAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}