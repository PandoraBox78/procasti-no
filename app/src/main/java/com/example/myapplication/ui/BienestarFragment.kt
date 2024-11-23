package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentBienestarBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class BienestarFragment : Fragment() {

    private var _binding: FragmentBienestarBinding? = null
    private val binding get() = _binding!!

    // Datos emocionales simulados
    private val emociones = mutableListOf<Emocion>(
        Emocion("Feliz", "Tuve un buen día", "2024-11-22"),
        Emocion("Triste", "Me sentí cansado", "2024-11-21"),
        Emocion("Tranquilo", "Día relajado", "2024-11-20")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBienestarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del RecyclerView para listar emociones
        binding.recyclerViewEmociones.layoutManager = LinearLayoutManager(requireContext())
        val emocionesAdapter = EmocionesAdapter(emociones)
        binding.recyclerViewEmociones.adapter = emocionesAdapter

        // Botón para agregar un nuevo registro emocional
        binding.fabAddEmocion.setOnClickListener {
            showAddEmotionDialog(emocionesAdapter)
        }

        // Configurar el gráfico de pastel
        setupPieChart()
    }

    private fun showAddEmotionDialog(adapter: EmocionesAdapter) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_emotion, null)

        // Crea un diálogo para agregar emociones
        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Agregar Emoción")
            .setView(dialogView)
            .setPositiveButton("Agregar") { _, _ ->
                val emocion = dialogView.findViewById<android.widget.Spinner>(R.id.spinnerEmocion).selectedItem.toString()
                val nota = dialogView.findViewById<android.widget.EditText>(R.id.inputNota).text.toString()
                val fecha = "2024-11-23" // Aquí puedes usar un selector de fecha si es necesario

                if (emocion.isEmpty()) {
                    Toast.makeText(requireContext(), "Selecciona una emoción", Toast.LENGTH_SHORT).show()
                } else {
                    val nuevaEmocion = Emocion(emocion, nota, fecha)
                    emociones.add(0, nuevaEmocion) // Agrega la emoción al inicio de la lista
                    adapter.notifyItemInserted(0)
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }

    private fun setupPieChart() {
        val pieChart = binding.pieChart
        val emocionesPorcentajes = mapOf(
            "Feliz" to 50f,
            "Triste" to 30f,
            "Tranquilo" to 20f
        )

        val entries = emocionesPorcentajes.map { PieEntry(it.value, it.key) }
        val dataSet = PieDataSet(entries, "Estado Emocional")
        dataSet.colors = listOf(
            resources.getColor(R.color.happy, null),
            resources.getColor(R.color.sad, null),
            resources.getColor(R.color.calm, null)
        )

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // Actualiza el gráfico
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
