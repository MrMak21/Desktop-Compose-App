package ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KteoScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    KteoScreenContent(state, onEvent)
}

@Composable
fun KteoScreenContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "KTEO"
        )
        SelectExcelFile(state, onEvent)
        SelectPdfFiles(state, onEvent)

        Spacer(modifier = Modifier.weight(1f))

        Button(
            enabled = state.filesList.isNotEmpty() && state.excelSelectedFile != null,
            onClick = {
            onEvent(MainContract.Event.AnalyzePDFKteo(selectedfiles = state.filesList))
        }) {
            Text("Analyze")
        }
    }

}

@Composable
fun SelectExcelFile(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Button(onClick = {
        onEvent(MainContract.Event.SelectExcelFile)
    }) {
        Text("Select EXCEL")
    }
    if (state.excelSelectedFile != null) {
        Text(text = state.excelSelectedFile.name)
    }
}

@Composable
fun SelectPdfFiles(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Column {
        Button(onClick = {
            onEvent(MainContract.Event.SelectFiles)
        }) {
            Text("Select PDF")
        }
        if (state.filesList.isNotEmpty()) {
            Text(text = "Selected files: ${state.filesList.size}")
        }
    }
}