package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class TareasAdapter(
    private val tareas: MutableList<Tarea>,
    private val onEditTarea: (Int) -> Unit,
    private val onDeleteTarea: (Int) -> Unit
) : RecyclerView.Adapter<TareasAdapter.TareaViewHolder>() {

    class TareaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tareaNombre)
        val prioridad: TextView = view.findViewById(R.id.tareaPrioridad)
        val fechaLimite: TextView = view.findViewById(R.id.tareaFecha)
        val checkCompletada: CheckBox = view.findViewById(R.id.checkCompletada)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TareaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tarea, parent, false)
        return TareaViewHolder(view)
    }

    override fun onBindViewHolder(holder: TareaViewHolder, position: Int) {
        val tarea = tareas[position]
        holder.nombre.text = tarea.nombre
        holder.prioridad.text = "Prioridad: ${tarea.prioridad}"
        holder.fechaLimite.text = "Fecha: ${tarea.fechaLimite}"
        holder.checkCompletada.isChecked = tarea.completada

        // Listener para editar la tarea
        holder.itemView.setOnClickListener {
            onEditTarea(position)
        }

        // Listener para eliminar la tarea (clic largo)
        holder.itemView.setOnLongClickListener {
            onDeleteTarea(position)
            true
        }
    }

    override fun getItemCount() = tareas.size
}

