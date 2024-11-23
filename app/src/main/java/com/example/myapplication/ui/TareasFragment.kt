package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTareasBinding

class TareasFragment : Fragment() {

    private var _binding: FragmentTareasBinding? = null
    private val binding get() = _binding!!

    private val tareas = mutableListOf<Tarea>() // Lista mutable para almacenar tareas
    private lateinit var tareasAdapter: TareasAdapter // Adaptador del RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializa la lista de tareas con datos de muestra
        tareas.addAll(getSampleTareas())

        // Configura el RecyclerView
        tareasAdapter = TareasAdapter(tareas, ::onEditTarea, ::onDeleteTarea)
        binding.recyclerViewTareas.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTareas.adapter = tareasAdapter

        // Configura el botón para agregar nuevas tareas
        binding.fabAddTarea.setOnClickListener {
            showAddTareaDialog()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showAddTareaDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)

        // Encuentra el Spinner
        val prioridadSpinner = dialogView.findViewById<Spinner>(R.id.spinnerPrioridad)

        // Configura el adaptador para el Spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridades, // Array de prioridades en strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioridadSpinner.adapter = adapter

        // Crea y muestra el diálogo
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar Tarea")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val nombre = dialogView.findViewById<EditText>(R.id.inputNombre).text.toString().trim()
                val prioridad = prioridadSpinner.selectedItem?.toString() ?: "Baja"
                val fecha = dialogView.findViewById<EditText>(R.id.inputFecha).text.toString().trim()

                // Validar que los campos no estén vacíos
                if (nombre.isEmpty() || fecha.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Crea la nueva tarea y agrégala a la lista
                val nuevaTarea = Tarea(nombre, prioridad, fecha, false)
                tareas.add(nuevaTarea)
                tareasAdapter.notifyItemInserted(tareas.size - 1)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }


    private fun onEditTarea(position: Int) {
        val tarea = tareas[position]
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_task, null)

        // Inicializar los elementos del diálogo
        val nombreInput = dialogView.findViewById<EditText>(R.id.inputNombre)
        val prioridadSpinner = dialogView.findViewById<Spinner>(R.id.spinnerPrioridad)
        val fechaInput = dialogView.findViewById<EditText>(R.id.inputFecha)

        // Configura el adaptador para el Spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridades, // Asegúrate de que esta referencia existe en strings.xml
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        prioridadSpinner.adapter = adapter

        // Asegúrate de que los valores de la tarea están asignados correctamente
        nombreInput.setText(tarea.nombre ?: "") // Nombre de la tarea (asegúrate de que no sea nulo)
        fechaInput.setText(tarea.fechaLimite ?: "") // Fecha límite

        // Configura el `Spinner` para que seleccione la prioridad actual de la tarea
        prioridadSpinner.setSelection(
            when (tarea.prioridad) {
                "Alta" -> 2
                "Media" -> 1
                else -> 0
            }
        )

        // Mostrar el diálogo para editar la tarea
        AlertDialog.Builder(requireContext())
            .setTitle("Editar Tarea")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoNombre = nombreInput.text.toString().trim()
                val nuevaPrioridad = prioridadSpinner.selectedItem?.toString() ?: "Baja"
                val nuevaFecha = fechaInput.text.toString().trim()

                // Validación: Asegúrate de que los campos no están vacíos
                if (nuevoNombre.isEmpty() || nuevaFecha.isEmpty()) {
                    Toast.makeText(requireContext(), "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Actualiza la tarea en la lista
                tareas[position] = tarea.copy(
                    nombre = nuevoNombre,
                    prioridad = nuevaPrioridad,
                    fechaLimite = nuevaFecha
                )
                tareasAdapter.notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }
    // Lógica para eliminar una tarea
    private fun onDeleteTarea(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Tarea")
            .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
            .setPositiveButton("Eliminar") { _, _ ->
                tareas.removeAt(position)
                tareasAdapter.notifyItemRemoved(position)
                tareasAdapter.notifyItemRangeChanged(position, tareas.size)
            }
            .setNegativeButton("Cancelar", null)
            .create()
            .show()
    }

    // Muestra una lista de tareas de prueba
    private fun getSampleTareas(): List<Tarea> {
        return listOf(
            Tarea("Tarea 1", "Alta", "2024-11-25", false),
            Tarea("Tarea 2", "Media", "2024-11-26", true),
            Tarea("Tarea 3", "Baja", "2024-11-27", false)
        )
    }
}


