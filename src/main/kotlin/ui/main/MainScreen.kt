package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import extensions.startCollecting
import operations.FileChooser
import org.koin.java.KoinJavaComponent.inject
import repository.PdfRepository
import ui.main.MainContract
import useCase.AnalyzePdfUseCase
import viewModel.main.MainViewModel

@Composable
@Preview
fun MainScreen() {

    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember{
        MainViewModel(coroutineScope = coroutineScope, pdfVehicleIdUseCase = AnalyzePdfUseCase(pdfRepository = PdfRepository()))
    }

    val state by viewModel.startCollecting()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        // Left side with a button
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            MainPickerContent(
                state = state,
                onEvent = viewModel::onEvent
            )
        }

        // Right side placeholder
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Gray) // Just for visualization
        ) {
            // Content for the right side
        }
    }
}

@Composable
fun MainPickerContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(state.selectedfiles) { index, item ->
                Text(text = item.name, modifier = Modifier.padding(8.dp))
            }
        }

        Row {
            Button(onClick = {
                onEvent(MainContract.Event.SelectFiles)
            }) {
                Text("Select PDF")
            }

            Button(
                enabled = state.selectedfiles.isNotEmpty(),
                onClick = {
                    onEvent(MainContract.Event.AnalyzePDF(selectedfiles = state.selectedfiles))
                }) {
                Text("Execute")
            }
        }
    }

}

