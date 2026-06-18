package com.example.listadecontatos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.listadecontatos.data.Contato
import com.example.listadecontatos.data.DatabaseHelper
import com.example.listadecontatos.data.Usuario

@Composable
fun ContatoFormScreen(
    dbHelper: DatabaseHelper,
    usuario: Usuario,
    contatoExistente: Contato?,
    onSalvar: () -> Unit,
    onCancelar: () -> Unit
) {
    var nome by remember { mutableStateOf(contatoExistente?.nome ?: "") }
    var telefone by remember { mutableStateOf(contatoExistente?.telefone ?: "") }
    var email by remember { mutableStateOf(contatoExistente?.email ?: "") }
    var erro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (contatoExistente == null) "Novo contato" else "Editar contato",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nome, onValueChange = { nome = it },
            label = { Text("Nome") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = telefone, onValueChange = { telefone = it },
            label = { Text("Telefone") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("E-mail (opcional)") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (erro != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = erro ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (nome.isBlank() || telefone.isBlank()) {
                    erro = "Nome e telefone são obrigatórios"
                } else if (contatoExistente == null) {
                    dbHelper.inserirContato(
                        Contato(
                            usuarioId = usuario.id,
                            nome = nome.trim(),
                            telefone = telefone.trim(),
                            email = email.trim()
                        )
                    )
                    onSalvar()
                } else {
                    dbHelper.atualizarContato(
                        contatoExistente.copy(
                            nome = nome.trim(),
                            telefone = telefone.trim(),
                            email = email.trim()
                        )
                    )
                    onSalvar()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onCancelar) {
            Text("Cancelar")
        }
    }
}
