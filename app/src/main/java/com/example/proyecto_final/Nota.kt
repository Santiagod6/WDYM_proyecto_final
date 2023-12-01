package com.example.proyecto_final

import com.google.firebase.database.Exclude

data class Nota(
    @get:Exclude
    var idNota: String? = null,
    val contenido: String?=null
)
