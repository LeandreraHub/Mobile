package com.example.listadecontatos.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.listadecontatos.data.Contato
import com.example.listadecontatos.data.DatabaseHelper
import com.example.listadecontatos.data.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaContatosScreen(
    dbHelper: DatabaseHelper,
    usuario: Usuario,
    onAdicionar: () -> Unit,
    onEditar: (Contato) -> Unit,
    onLogout: () -> Unit
) {
    var contatos by remember { mutableStateOf(dbHelper.listarContatos(usuario.id)) }
    var contatoParaExcluir by remember { mutableStateOf<Contato?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Olá, ${usuario.nome}") },
                actions = {
                    TextButton(onClick = onLogout) { Text("Sair") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdicionar) {
                Text("+")
            }
        }
    ) { padding ->
        if (contatos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhum contato cadastrado ainda")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(contatos) { contato ->
                    ListItem(
                        headlineContent = { Text(contato.nome) },
                        supportingContent = {
                            Text(
                                if (contato.email.isNotBlank())
                                    "${contato.telefone} · ${contato.email}"
                                else
                                    contato.telefone
                            )
                        },
                        trailingContent = {
                            Row {
                                TextButton(onClick = { onEditar(contato) }) {
                                    Text("Editar")
                                }
                                TextButton(onClick = { contatoParaExcluir = contato }) {
                                    Text("Excluir")
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }

    contatoParaExcluir?.let { contato ->
        AlertDialog(
            onDismissRequest = { contatoParaExcluir = null },
            title = { Text("Excluir contato") },
            text = { Text("Tem certeza que deseja excluir ${contato.nome}?") },
            confirmButton = {
                TextButton(onClick = {
                    dbHelper.excluirContato(contato.id)
                    contatoParaExcluir = null
                    contatos = dbHelper.listarContatos(usuario.id)
                }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { contatoParaExcluir = null }) { Text("Cancelar") }
            }
        )
    }
}
