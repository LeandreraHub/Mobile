package com.example.listadecontatos.data

data class Usuario(
    val id: Long,
    val nome: String,
    val email: String
)

data class Contato(
    val id: Long = 0,
    val usuarioId: Long,
    val nome: String,
    val telefone: String,
    val email: String = ""
)
