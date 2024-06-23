package ui.trafficLicense

import data.main.PdfListFile
import ui.common.UiEvent
import ui.common.UiState

interface TrafficLicenseContract {

    sealed class Event: UiEvent {
        object SelectFiles: Event()
        object SelectExcelFile: Event()
        data class AnalyzeTrafficLicenses(val listFiles: List<PdfListFile>): Event()
    }

    data class State(
        val filesList: List<PdfListFile> = emptyList(),
    ): UiState
}