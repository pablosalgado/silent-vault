package io.github.pablosalgado.silent.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import io.github.pablosalgado.silent.vault.data.NotificationRepository
import io.github.pablosalgado.silent.vault.data.local.NotificationDatabase
import io.github.pablosalgado.silent.vault.ui.MainScreen
import io.github.pablosalgado.silent.vault.ui.MainViewModel
import io.github.pablosalgado.silent.vault.ui.theme.SilentVaultTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = NotificationDatabase.getInstance(this)
        val repository = NotificationRepository(db.notificationDao())
        viewModel = ViewModelProvider(this, MainViewModel.factory(repository))[MainViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            SilentVaultTheme {
                MainScreen(viewModel)
            }
        }
    }
}
