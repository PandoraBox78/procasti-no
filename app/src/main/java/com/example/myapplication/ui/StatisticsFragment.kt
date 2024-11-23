package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.FragmentStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var sessionsCompletedToday = 3
    private val sessionsPerDay = listOf(3, 4, 2, 5, 1, 6, 7) // Datos de ejemplo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizar resumen de estadísticas
        binding.tvStatisticsSummary.text = "Sesiones completadas hoy: $sessionsCompletedToday"

        // Configurar gráfico de barras
        setupBarChart(binding.barChart)
    }

    private fun setupBarChart(barChart: BarChart) {
        val entries = sessionsPerDay.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Sesiones Completadas")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val barData = BarData(barDataSet)
        barChart.data = barData

        // Configuración del gráfico
        barChart.description.text = "Progreso Semanal"
        barChart.setFitBars(true)
        barChart.animateY(1000)

        // Configuración del eje X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Nombres para los días de la semana
        xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
            private val days = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index in days.indices) days[index] else ""
            }
        }


        barChart.invalidate() // Refresca el gráfico
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
