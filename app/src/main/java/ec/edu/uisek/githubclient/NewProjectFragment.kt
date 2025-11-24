package ec.edu.uisek.githubclient

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
// CAMBIO 1: La clase de binding generada usa el nombre del nuevo layout
import ec.edu.uisek.githubclient.databinding.FragmentNewProjectBinding

class NewProjectFragment : Fragment() {

    // CAMBIO 1: La clase de binding ahora es FragmentNewProjectBinding
    private var _binding: FragmentNewProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // CAMBIO 1: Inflar el nuevo layout (FragmentNewProjectBinding se generó de fragment_new_project.xml)
        _binding = FragmentNewProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // CAMBIO 2: Usar el nuevo ID del botón (btnSaveNewProject)
        binding.btnSaveNewProject.setOnClickListener {
            // CAMBIO 3: Usar el nuevo ID para el campo de nombre (etNewProjectName)
            val projectName = binding.etNewProjectName.text.toString()
            if (projectName.isNotBlank()) {
                // CAMBIO 4: Usar la nueva string para el mensaje de éxito
                Toast.makeText(context, getString(R.string.msg_project_saved_ok), Toast.LENGTH_SHORT).show()

                parentFragmentManager.popBackStack()
            } else {
                // CAMBIO 3 & 4: Usar el nuevo ID y la nueva string para el hint de error
                binding.etNewProjectName.error = getString(R.string.hint_new_project_name)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}