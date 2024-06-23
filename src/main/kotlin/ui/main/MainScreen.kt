package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import extensions.startCollecting
import repository.PdfRepository
import ui.main.KteoScreenContent
import ui.main.MainContract
import ui.main.MainContract.Event.SideMenuItemSelected
import ui.sidePanel.SidePanel
import ui.sidePanel.SidePanelItem
import ui.trafficFees.TrafficFeesScreen
import ui.trafficLicense.TrafficLicenseScreen
import useCase.AnalyzePdfKteoUseCase
import useCase.AnalyzePdfVehicleIdUseCase
import useCase.RenamePdfUseCase
import viewModel.main.MainViewModel

@Composable
@Preview
fun MainScreen() {

    val coroutineScope = rememberCoroutineScope()
    val viewModel = remember {
        MainViewModel(
            coroutineScope = coroutineScope,
            pdfVehicleIdUseCase = AnalyzePdfVehicleIdUseCase(pdfRepository = PdfRepository()),
            renamePdfUseCase = RenamePdfUseCase(pdfRepository = PdfRepository()),
            pdfKteoUseCase = AnalyzePdfKteoUseCase(pdfRepository = PdfRepository())
        )
    }

    val state by viewModel.startCollecting()

    MainScreenContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}

@Composable
fun MainScreenContent(
    state: MainContract.State,
    onEvent: (MainContract.Event) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Side Panel on the left
        SidePanel { menuSelected ->
            onEvent(SideMenuItemSelected(menuItem = menuSelected))
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (state.sidePanelItem) {
                SidePanelItem.TRAFFIC_FEES -> { TrafficFeesScreen(state, onEvent) }
                SidePanelItem.TRAFFIC_LICENSE -> { TrafficLicenseScreen() }
                SidePanelItem.KTEO -> { KteoScreenContent(state, onEvent) }
            }
        }
    }
}



