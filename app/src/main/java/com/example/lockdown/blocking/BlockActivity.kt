package com.example.lockdown.blocking

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lockdown.ui.theme.LockdownTheme
import com.example.lockdown.MainActivity

class BlockActivity : ComponentActivity() {
    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // El botón atrás no debe regresar a la app bloqueada
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goHome()
        })

        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: ""
        val appLabel = try {
            packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(blockedPackage, 0)
            ).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            blockedPackage
        }

        setContent {
            LockdownTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BlockScreenContent(appLabel = appLabel, onGoHome = { goHome() }, onRequestUnlock = { requestUnlock() })
                }
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    private fun requestUnlock() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(MainActivity.EXTRA_OPEN_CHALLENGE, true)
        }
        startActivity(intent)
        finish()
    }
}

@Composable
private fun BlockScreenContent(appLabel: String, onGoHome: () -> Unit, onRequestUnlock: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("$appLabel está bloqueada", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))
        Text(
            "Fuera del horario que configuraste para esta app.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = onGoHome) { Text("Ir al inicio") }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onRequestUnlock) { Text("Solicitar desbloqueo") }
    }
}
