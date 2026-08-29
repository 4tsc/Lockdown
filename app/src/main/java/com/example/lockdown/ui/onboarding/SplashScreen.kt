package com.example.lockdown.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.lockdown.R
import kotlinx.coroutines.delay
import kotlin.collections.random

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val backgroundRes = remember {
        listOf(R.drawable.fondolockdown, R.drawable.fondolockdowndos).random()
    }

    LaunchedEffect(Unit) {
        delay(3000L)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}