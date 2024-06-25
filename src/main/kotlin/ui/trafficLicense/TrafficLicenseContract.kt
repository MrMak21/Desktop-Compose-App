package ui.trafficLicense

import data.main.PdfListFile
import ui.common.UiEvent
import ui.common.UiState
import java.io.File

interface TrafficLicenseContract {

    sealed class Event: UiEvent {
        object SelectFiles: Event()
        object SelectExcelFile: Event()
        object ClearSelectedExcelFile: Event()
        object ClearSelectedPdfFiles: Event()
        data class AnalyzeTrafficLicenses(val listFiles: List<PdfListFile>): Event()
        data class OpenFileExplorer(val file: PdfListFile): Event()
    }

    data class State(
        override val testStateInt: Int = 0,
        val filesList: List<PdfListFile> = emptyList(),
        val excelSelectedFile: File? = null,
    ): UiState
}