package ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import data.main.PdfListFile
import java.io.File

@Composable
fun SecondaryMainContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {

    Column {
        Row {
            Button(onClick = {
                onEvent(MainContract.Event.SelectExcelFile)
            }) {
                Text("Select EXCEL")
            }
            if (state.excelSelectedFile != null) {
                Text(text = state.excelSelectedFile.name)
            }
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