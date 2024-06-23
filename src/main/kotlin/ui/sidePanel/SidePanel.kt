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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import theme.*
import ui.sidePanel.SidePanelItem.*

@Composable
fun SidePanel(
    onMenuSelected: (SidePanelItem) -> Unit
) {
    Row {
        Column(
            modifier = Modifier
                .width(100.dp)
                .fillMaxHeight()
                .background(backgroundGraySecondary),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SidePanelItem(TRAFFIC_FEES, "assets/ic_car_plate.png", onMenuSelected)
            SidePanelItem(KTEO, "assets/ic_kteo.png", onMenuSelected, enabled = false)
            SidePanelItem(TRAFFIC_LICENSE, "assets/ic_kteo.png", onMenuSelected)

        }
        Divider(modifier = Modifier.fillMaxHeight().width(1.dp).background(backgroundGraySecondary))
    }
}

@Composable
fun SidePanelItem(
    itemType: SidePanelItem,
    iconPath: String,
    onMenuSelected: (SidePanelItem) -> Unit,
    enabled: Boolean = true
) {
    Box(
        Modifier.clickable {
            if (enabled) { onMenuSelected(itemType) }
        }.alpha(if (enabled) 1f else 0.4f),
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.padding(top = 16.dp).size(30.dp),
                painter = painterResource(iconPath),
                contentDescription = "",
                colorFilter = ColorFilter.tint(letterGray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getItemTitle(itemType),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = letterGray
            )
            Divider(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(1.dp).background(backgroundGraySecondary)
            )
        }
    }
}

private fun getItemTitle(type: SidePanelItem): String {
    return when (type) {
        KTEO -> {
            "KTEO"
        }
        TRAFFIC_FEES -> {
            "B.TK"
        }
        TRAFFIC_LICENSE -> {
            "ΑΔΕΙΕΣ"
        }
    }
}

enum class SidePanelItem {
    KTEO,
    TRAFFIC_FEES,
    TRAFFIC_LICENSE
}