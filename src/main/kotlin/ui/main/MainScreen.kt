package ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.main.PdfListFile
import extensions.startCollecting
import repository.PdfRepository
import ui.main.FileRow
import ui.main.KteoScreenContent
import ui.main.MainContract
import ui.main.SecondaryMainContent
import ui.sidePanel.SidePanel
import useCase.AnalyzePdfVehicleIdUseCase
import useCase.RenamePdfUseCase
import viewModel.main.MainViewModel
import ui.main.MainContract.Event.*
import ui.sidePanel.SidePanelItem
import ui.trafficFees.TrafficFeesScreen
import useCase.AnalyzePdfKteoUseCase

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
                SidePanelItem.INVOICE -> { SecondaryMainContent(state, onEvent) }
                SidePanelItem.KTEO -> { KteoScreenContent(state, onEvent) }
            }
        }
    }
}



