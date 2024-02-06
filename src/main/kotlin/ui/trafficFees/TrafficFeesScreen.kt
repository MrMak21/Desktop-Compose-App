package ui.trafficFees

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.main.PdfListFile
import ui.main.FailedFileRow
import ui.main.FileRow
import ui.main.MainContract

@Composable
fun TrafficFeesScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            text = "ΤΕΛΗ ΚΥΚΛΟΦΟΡΙΑΣ"
        )

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

            Button(onClick = {
                onEvent(MainContract.Event.SelectExcelFile)
            }) {
                Text("Select EXCEL")
            }
            if (state.excelSelectedFile != null) {
                Text(text = state.excelSelectedFile.name)
            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                itemsIndexed(state.failedFiles) { index, item ->
                    FailedFileRow(file = item) { clickedFile: PdfListFile ->
                        onEvent(MainContract.Event.OpenFileExplorer(clickedFile))
                    }
                }
            }
        }
    }
}