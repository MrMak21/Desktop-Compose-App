package ui.main

import data.main.PdfListFile
import ui.common.UiEvent
import ui.common.UiState
import ui.sidePanel.SidePanelItem
import java.io.File

interface MainContract {

    sealed class Event: UiEvent {

        data class AnalyzePDFVehicleId(val selectedfiles: List<PdfListFile>): Event()
        data class AnalyzePDFKteo(val selectedfiles: List<PdfListFile>): Event()
        object SelectFiles: Event()
        object SelectExcelFile: Event()
        data class OpenFileExplorer(val file: PdfListFile): Event()
        data class SideMenuItemSelected(val menuItem: SidePanelItem): Event()
    }

    data class State(
        val mainState: MainScreenState = MainScreenState.None,
        val selectedfiles: List<File> = emptyList(),
        val failedFiles: List<PdfListFile> = emptyList(),
        val filesList: List<PdfListFile> = emptyList(),
        val excelSelectedFile: File? = null,
        val sidePanelItem: SidePanelItem = SidePanelItem.TRAFFIC_FEES,
        val testStateInt: Int = 0
    ): UiState

    sealed class MainScreenState {
        object None: MainScreenState()
        object Loading: MainScreenState()
        data class Error(val error: Throwable): MainScreenState()
        data class Success(val isSuccess: Boolean): MainScreenState()
    }
}