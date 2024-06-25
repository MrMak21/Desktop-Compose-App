// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import koin.modules.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import ui.main.MainScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen()
    }
}


fun main() = application {
    startKoin {
        modules(appModule)
    }

    Window(
        state = rememberWindowState(width = 1000.dp, height = 700.dp), // Set fixed size here
        resizable = false,
        title = "Kyklos SA.",
        icon = painterResource("assets/ic_logo.png"),
        onCloseRequest = ::exitApplication) {
        App()
    }
}


