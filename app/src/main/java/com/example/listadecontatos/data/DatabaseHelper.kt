package com.example.listadecontatos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.security.MessageDigest

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                senha_hash TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE contatos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario_id INTEGER NOT NULL,
                nome TEXT NOT NULL,
                telefone TEXT NOT NULL,
                email TEXT,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS contatos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun registrarUsuario(nome: String, email: String, senha: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", nome)
            put("email", email)
            put("senha_hash", hashSenha(senha))
        }
        val id = db.insert("usuarios", null, values)
        return id != -1L
    }

    fun login(email: String, senha: String): Usuario? {
        val db = readableDatabase
        val cursor = db.query(
            "usuarios", arrayOf("id", "nome", "email", "senha_hash"),
            "email = ?", arrayOf(email), null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                val senhaHash = it.getString(it.getColumnIndexOrThrow("senha_hash"))
                if (senhaHash == hashSenha(senha)) {
                    return Usuario(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        nome = it.getString(it.getColumnIndexOrThrow("nome")),
                        email = it.getString(it.getColumnIndexOrThrow("email"))
                    )
                }
            }
        }
        return null
    }

    fun inserirContato(contato: Contato): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("usuario_id", contato.usuarioId)
            put("nome", contato.nome)
            put("telefone", contato.telefone)
            put("email", contato.email)
        }
        return db.insert("contatos", null, values)
    }

    fun listarContatos(usuarioId: Long): List<Contato> {
        val lista = mutableListOf<Contato>()
        val db = readableDatabase
        val cursor = db.query(
            "contatos", null, "usuario_id = ?", arrayOf(usuarioId.toString()),
            null, null, "nome ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    Contato(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        usuarioId = it.getLong(it.getColumnIndexOrThrow("usuario_id")),
                        nome = it.getString(it.getColumnIndexOrThrow("nome")),
                        telefone = it.getString(it.getColumnIndexOrThrow("telefone")),
                        email = it.getString(it.getColumnIndexOrThrow("email")) ?: ""
                    )
                )
            }
        }
        return lista
    }

    fun atualizarContato(contato: Contato): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nome", contato.nome)
            put("telefone", contato.telefone)
            put("email", contato.email)
        }
        return db.update("contatos", values, "id = ?", arrayOf(contato.id.toString()))
    }

    fun excluirContato(id: Long): Int {
        val db = writableDatabase
        return db.delete("contatos", "id = ?", arrayOf(id.toString()))
    }

    companion object {
        private const val DATABASE_NAME = "lista_contatos.db"
        private const val DATABASE_VERSION = 1

        fun hashSenha(senha: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(senha.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }
}
