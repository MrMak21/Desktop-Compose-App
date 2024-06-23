package ui.trafficFees

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.main.PdfListFile
import theme.*
import ui.dialog.CustomAlertDialog
import ui.main.FailedFileRow
import ui.main.FileRow
import ui.main.MainContract
import java.awt.Cursor

@Composable
fun TrafficFeesScreen(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {

    val showInfoDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundWhite),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 20.dp),
                    text = "ΤΕΛΗ ΚΥΚΛΟΦΟΡΙΑΣ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Image(
                modifier = Modifier.size(18.dp)
                    .clickable { showInfoDialog.value = true }
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                painter = painterResource("assets/ic_info.png"),
                contentDescription = "",
                colorFilter = ColorFilter.tint(Color.Gray)
            )
        }


        Divider(
            modifier = Modifier.fillMaxWidth().padding(start = 60.dp, end = 60.dp, bottom = 30.dp),
            color = backgroundGraySecondary
        )

        if (showInfoDialog.value) {
            CustomAlertDialog(onDismiss = { showInfoDialog.value = false} )
        }


        if (state.filesList.isEmpty()) {
            val stroke = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .drawBehind {
                        drawRoundRect(color = Color.Gray, style = stroke, cornerRadius = CornerRadius(8.dp.toPx()))
                    }
                    .clickable { onEvent(MainContract.Event.SelectFiles) }
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 80.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Select PDF files", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Selected files: ${state.filesList.size}", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        modifier = Modifier.size(12.dp)
                        .clickable { onEvent(MainContract.Event.ClearSelectedPdfFiles) }
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))), painter = painterResource("assets/ic_close.png"), contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(200.dp),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        top = 16.dp,
                        end = 12.dp,
                        bottom = 16.dp
                    )) {
                    itemsIndexed(state.filesList) { index, item ->
                        FileRow(file = item) { clickedFile: PdfListFile ->
                            onEvent(MainContract.Event.OpenFileExplorer(clickedFile))
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth().background(backgroundGraySecondary).padding(vertical = 16.dp, horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(modifier = Modifier.size(20.dp), painter = painterResource("assets/ic_excel.png"), contentDescription = "")
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    modifier = Modifier.clickable {
                        onEvent(MainContract.Event.SelectExcelFile)
                    }
                    .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                    text = if (state.excelSelectedFile == null) "Select Excel file" else state.excelSelectedFile.name,
                    color = if (state.excelSelectedFile != null) excelGreen else Color.Blue,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (state.excelSelectedFile != null) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(
                        modifier = Modifier.size(12.dp)
                            .clickable { onEvent(MainContract.Event.ClearSelectedExcelFile) }
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                        painter = painterResource("assets/ic_close.png"),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.Gray))
                }
            }

            OutlinedButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                enabled = state.filesList.isNotEmpty(),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = activeGreen,
                    disabledBackgroundColor = activeGreen.copy(alpha = 0.4f)
                ),
                onClick = {
                    onEvent(MainContract.Event.RenameTrafficFeesFiles(selectedfiles = state.filesList))
                }) {
                Text(
                    text = "RENAME",
                    fontSize = 16.sp,
                    color = letterWhiteSnow,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            OutlinedButton(
                modifier = Modifier.pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                enabled = state.filesList.isNotEmpty() && state.excelSelectedFile != null,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = activeGreen,
                    disabledBackgroundColor = activeGreen.copy(alpha = 0.4f)
                ),
                onClick = {
                    onEvent(MainContract.Event.AnalyzePDFVehicleId(selectedfiles = state.filesList))
                }) {
                Text(
                    text = "SUBMIT",
                    fontSize = 16.sp,
                    color = letterWhiteSnow,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}