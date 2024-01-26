package ui.sidePanel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ui.sidePanel.SidePanelItem.*

@Composable
fun SidePanel(
    onMenuSelected: (SidePanelItem) -> Unit
) {
    Row {
        Column(
            modifier = Modifier
                .width(70.dp)
                .fillMaxHeight()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SidePanelItem(KTEO, onMenuSelected)
            SidePanelItem(TRAFFIC_FEES, onMenuSelected)
            SidePanelItem(INVOICE, onMenuSelected)

        }
        Divider(modifier = Modifier.fillMaxHeight().width(1.dp).background(Color.Gray))
    }
}

@Composable
fun SidePanelItem(
    itemType: SidePanelItem,
    onMenuSelected: (SidePanelItem) -> Unit
) {
    Column(
        modifier = Modifier.padding(top = 8.dp).clickable { onMenuSelected(itemType) },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource("assets/ic_pdf.png"),
            contentDescription = "",
            modifier = Modifier.size(18.dp)
        )
        Text(text = getItemTitle(itemType))
        Divider(modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(1.dp).background(Color.Gray))
    }
}

private fun getItemTitle(type: SidePanelItem): String {
    return when (type) {
        KTEO -> {
            "KTEO"
        }
        TRAFFIC_FEES -> {
            "T.Fees"
        }
        INVOICE -> {
            "Invoice"
        }
    }
}

enum class SidePanelItem {
    KTEO,
    TRAFFIC_FEES,
    INVOICE
}