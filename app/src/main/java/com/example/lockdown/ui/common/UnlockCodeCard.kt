package com.example.lockdown.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.lockdown.util.UnlockManager

@Composable
fun UnlockCodeCard(onUnlocked: () -> Unit) {
    val context = LocalContext.current
    var code by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "El juramento ha sido sellado. Tus pecados, desterrados...",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Pero si por algun motivo, tu corazon se doblegara ante el deseo...",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Invoca al señor de Temuco, el tiene la clave.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(60.dp))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it; error = false },
            label = { Text("Código") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = error,
            singleLine = true
        )
        if (error) {
            Spacer(Modifier.height(4.dp))
            Text("Código incorrecto", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = {
            if (UnlockManager.tryUnlock(context, code)) onUnlocked() else error = true
        }) { Text("Desbloquear") }
        Spacer(Modifier.height(25.dp))
        Text(
            "Las aplicaciones seleccionadas han sido bloqueadas. Se generó un código de un solo uso, de entre 1 y 32 caracteres. Llama al Pollo y negocia el código con él. Entonces serás libre por el resto del día.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}