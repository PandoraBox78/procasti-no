package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class EmocionesAdapter(private val emociones: List<Emocion>) :
    RecyclerView.Adapter<EmocionesAdapter.EmocionViewHolder>() {

    class EmocionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tipo: TextView = view.findViewById(R.id.textTipoEmocion)
        val nota: TextView = view.findViewById(R.id.textNotaEmocion)
        val fecha: TextView = view.findViewById(R.id.textFechaEmocion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmocionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_emocion, parent, false)
        return EmocionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmocionViewHolder, position: Int) {
        val emocion = emociones[position]
        holder.tipo.text = emocion.tipo
        holder.nota.text = emocion.nota
        holder.fecha.text = emocion.fecha
    }

    override fun getItemCount() = emociones.size
}
