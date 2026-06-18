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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.listadecontatos.data.DatabaseHelper

@Composable
fun CadastroScreen(
    dbHelper: DatabaseHelper,
    onCadastroSucesso: () -> Unit,
    onVoltar: () -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Criar conta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = nome, onValueChange = { nome = it },
            label = { Text("Nome") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("E-mail") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = senha, onValueChange = { senha = it },
            label = { Text("Senha") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmarSenha, onValueChange = { confirmarSenha = it },
            label = { Text("Confirmar senha") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (erro != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = erro ?: "", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                when {
                    nome.isBlank() || email.isBlank() || senha.isBlank() ->
                        erro = "Preencha todos os campos"
                    senha != confirmarSenha ->
                        erro = "As senhas não coincidem"
                    else -> {
                        val sucesso = dbHelper.registrarUsuario(nome.trim(), email.trim(), senha)
                        if (sucesso) {
                            onCadastroSucesso()
                        } else {
                            erro = "Esse e-mail já está cadastrado"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cadastrar")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onVoltar) {
            Text("Já tem conta? Entrar")
        }
    }
}
