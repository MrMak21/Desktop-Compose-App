package ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import data.main.PdfFileStatus
import data.main.PdfListFile
import java.awt.Cursor
import java.io.File

@Composable
fun FileRow(
    file: PdfListFile,
    onClick: (PdfListFile) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
            .clickable { onClick(file) }
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
    ) {
        Image(painter = painterResource("assets/ic_pdf_red.png"), contentDescription = "", modifier = Modifier.size(18.dp))
        Text(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
            text = file.file.name,
            color = Color.Blue,
            fontSize = 16.sp
        )
        when (file.status) {
            PdfFileStatus.LOADING -> {
                Image(painter = painterResource("assets/ic_loading.png"), contentDescription = "", modifier = Modifier.size(18.dp))
            }
            PdfFileStatus.SUCCESS -> {
                Image(painter = painterResource("assets/ic_success.svg"), contentDescription = "", modifier = Modifier.size(18.dp))
            }
            PdfFileStatus.FAILED -> {
                Image(painter = painterResource("assets/ic_error.png"), contentDescription = "", modifier = Modifier.size(18.dp))
            }
            else -> {}
        }
    }
}

@Composable
fun FailedFileRow(
    file: PdfListFile,
    onClick: (PdfListFile) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 16.dp)
            .clickable { onClick(file) }
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))
    ) {
        Image(painter = painterResource("assets/ic_error_file.png"), contentDescription = "", modifier = Modifier.size(18.dp))
        Text(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 12.dp),
            text = file.file.name,
            color = Color.Blue,
            fontSize = 16.sp
        )
    }
}