package com.example.listadecontatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.listadecontatos.data.Contato
import com.example.listadecontatos.data.DatabaseHelper
import com.example.listadecontatos.data.Usuario
import com.example.listadecontatos.ui.CadastroScreen
import com.example.listadecontatos.ui.ContatoFormScreen
import com.example.listadecontatos.ui.ListaContatosScreen
import com.example.listadecontatos.ui.LoginScreen
import com.example.listadecontatos.ui.theme.ListaDeContatosTheme

private sealed class Tela {
    object Login : Tela()
    object Cadastro : Tela()
    object Lista : Tela()
    data class Formulario(val contato: Contato?) : Tela()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dbHelper = DatabaseHelper(this)
        setContent {
            ListaDeContatosTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(dbHelper)
                }
            }
        }
    }
}

@Composable
private fun AppNavigation(dbHelper: DatabaseHelper) {
    var tela by remember { mutableStateOf<Tela>(Tela.Login) }
    var usuarioLogado by remember { mutableStateOf<Usuario?>(null) }

    when (val telaAtual = tela) {
        is Tela.Login -> LoginScreen(
            dbHelper = dbHelper,
            onLoginSucesso = { usuario ->
                usuarioLogado = usuario
                tela = Tela.Lista
            },
            onIrParaCadastro = { tela = Tela.Cadastro }
        )

        is Tela.Cadastro -> CadastroScreen(
            dbHelper = dbHelper,
            onCadastroSucesso = { tela = Tela.Login },
            onVoltar = { tela = Tela.Login }
        )

        is Tela.Lista -> ListaContatosScreen(
            dbHelper = dbHelper,
            usuario = usuarioLogado!!,
            onAdicionar = { tela = Tela.Formulario(null) },
            onEditar = { contato -> tela = Tela.Formulario(contato) },
            onLogout = {
                usuarioLogado = null
                tela = Tela.Login
            }
        )

        is Tela.Formulario -> ContatoFormScreen(
            dbHelper = dbHelper,
            usuario = usuarioLogado!!,
            contatoExistente = telaAtual.contato,
            onSalvar = { tela = Tela.Lista },
            onCancelar = { tela = Tela.Lista }
        )
    }
}
