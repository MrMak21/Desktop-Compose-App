package viewModel.trafficLicense

import data.main.PdfFileStatus
import data.main.PdfListFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import operations.FileChooser
import ui.main.MainContract
import ui.trafficLicense.TrafficLicenseContract.*
import ui.trafficLicense.TrafficLicenseContract.Event
import ui.trafficLicense.TrafficLicenseContract.Event.*
import ui.trafficLicense.TrafficLicenseContract.State
import useCase.AnalyzePdfTrafficLicenseUseCase
import viewModel.core.CoreViewModel

class TrafficLicenseViewModel(
    private val coroutineScope: CoroutineScope,
    private val analyzePdfTrafficLicenseUseCase: AnalyzePdfTrafficLicenseUseCase
): CoreViewModel<Event, State>(
    initialState = State(),
    coroutineScope = coroutineScope
) {

    override suspend fun handleEvent(event: Event) {
        when (event) {
            is SelectFiles -> {
                selectFiles()
            }
            is SelectExcelFile -> {

            }
            is AnalyzeTrafficLicenses -> {
                analyzeTrafficLicenseFiles(event.listFiles)
            }
        }
    }

    private fun analyzeTrafficLicenseFiles(listFiles: List<PdfListFile>) {
        coroutineScope.launch {

            listFiles.forEach {
                val vehicleId = analyzePdfTrafficLicenseUseCase.invoke(it.file)
                println("Found adeies: $vehicleId")
            }
        }
    }


    private fun selectFiles() {
        val fileChooser = FileChooser()
        val selectedFiles = fileChooser.selectMutliplePDF()
        setState {
            copy(
                filesList = selectedFiles.map {
                    PdfListFile(
                        file = it,
                        status = PdfFileStatus.SELECTED
                    )
                } as MutableList<PdfListFile>
            )
        }
    }
}