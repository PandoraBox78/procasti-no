package com.example.myapplication.ui

data class Tarea(
    val nombre: String,
    val prioridad: String, // Baja, Media, Alta
    val fechaLimite: String,
    var completada: Boolean
)

