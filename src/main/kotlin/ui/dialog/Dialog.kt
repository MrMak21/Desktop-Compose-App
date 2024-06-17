package ui.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.Key.Companion.R
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.skia.Color
import theme.letterGray
import java.awt.Cursor

@Composable
fun CustomAlertDialog(onDismiss: () -> Unit) {

    AlertDialog(
        modifier = Modifier.clip(RoundedCornerShape(12.dp)),
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "How to use",
                    fontSize = 18.sp,
                    color = letterGray,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    buildAnnotatedString {
                        append("1) Select the PDF files that contain the vehicle license plates\n")
                        append("2) Select the Excel file that you want to modify based on the above files\n")
                        append("3) Click Submit")
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = letterGray,
                    textAlign = TextAlign.Left
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    modifier = Modifier
                        .clickable { onDismiss() }
                        .padding(8.dp)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR))),
                    text = "OK",
                    color = androidx.compose.ui.graphics.Color.Blue,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )

}