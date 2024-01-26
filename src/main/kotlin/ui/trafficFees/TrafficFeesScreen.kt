package ui.trafficFees

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import data.main.PdfListFile
import ui.main.FileRow
import ui.main.MainContract

@Composable
fun TrafficFeesScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(state.filesList) { index, item ->
                FileRow(file = item) { clickedFile: PdfListFile ->
                    onEvent(MainContract.Event.OpenFileExplorer(clickedFile))
                }
            }
        }

        Row {
            Button(onClick = {
                onEvent(MainContract.Event.SelectFiles)
            }) {
                Text("Select PDF")
            }

            Button(
                enabled = state.filesList.isNotEmpty() && state.excelSelectedFile != null,
                onClick = {
                    onEvent(MainContract.Event.AnalyzePDFVehicleId(selectedfiles = state.filesList))
                }) {
                Text("Telh Kykloforias")
            }
        }
    }
}