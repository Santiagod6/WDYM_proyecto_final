package com.example.proyecto_final

import com.google.firebase.database.Exclude

data class NotaFavorita(
    @get:Exclude
    var idNota: String? = null,
    val contenido: String?=null
)
