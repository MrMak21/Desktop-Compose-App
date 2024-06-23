package viewModel.trafficLicense

import data.main.PdfFileStatus
import data.main.PdfListFile
import extensions.onError
import extensions.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import operations.FileChooser
import operations.excel.ExcelHandler
import org.apache.poi.ss.usermodel.WorkbookFactory
import ui.main.MainContract
import ui.trafficLicense.TrafficLicenseContract.*
import ui.trafficLicense.TrafficLicenseContract.Event
import ui.trafficLicense.TrafficLicenseContract.Event.*
import ui.trafficLicense.TrafficLicenseContract.State
import useCase.AnalyzePdfTrafficLicenseUseCase
import useCase.RenamePdfUseCase
import viewModel.core.CoreViewModel
import java.io.File

class TrafficLicenseViewModel(
    private val coroutineScope: CoroutineScope,
    private val analyzePdfTrafficLicenseUseCase: AnalyzePdfTrafficLicenseUseCase,
    private val renamePdfUseCase: RenamePdfUseCase,
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
                selectExcelFile()
            }
            is AnalyzeTrafficLicenses -> {
                analyzeTrafficLicenseFiles(event.listFiles)
            }
            is ClearSelectedExcelFile -> {
                clearSelectedExcelFile()
            }
        }
    }

    private fun analyzeTrafficLicenseFiles(listFiles: List<PdfListFile>) {
        if (listFiles.isEmpty())
            return

        val excelHandler = currentState.excelSelectedFile?.let { ExcelHandler.initExcelHandler(it) } ?: return

        coroutineScope.launch(Dispatchers.IO) {
            listFiles.forEach {
                renameAndWriteToExcelTrafficFeesFile(workingFile = it, excelHandler = excelHandler)
            }
        }
    }

    private suspend fun renameAndWriteToExcelTrafficFeesFile(workingFile: PdfListFile, excelHandler: ExcelHandler) {
        setStateToFile(workingFile, PdfFileStatus.LOADING)

        val vehicleId = analyzePdfTrafficLicenseUseCase.invoke(workingFile.file)
        println("Found adeies: $vehicleId")

        if (vehicleId == null) {
            setStateToFile(workingFile, PdfFileStatus.FAILED)
            return
        }

        renamePdfUseCase.invoke(workingFile.file, vehicleId)
            .onSuccess { renamedFile ->
                onFileRenameSuccess(workingFile, renamedFile)
            }
            .onError {
                setStateToFile(workingFile, PdfFileStatus.FAILED)
                return@onError
            } ?: return

        val writtenToExcel = excelHandler.writeTelhKykloforiasToExcel(vehicleId)
        setStateToFile(
            file = workingFile,
            newFileStatus = if (writtenToExcel) PdfFileStatus.SUCCESS else PdfFileStatus.FAILED
        )
    }

    override fun setStateToFile(file: PdfListFile, newFileStatus: PdfFileStatus) {
        currentState.filesList.find {it == file}?.changeStatus(newStatus = newFileStatus)
        setState {
            copy(
                testStateInt = currentState.testStateInt + 1,
                filesList = currentState.filesList
            )
        }
    }


    private fun clearSelectedExcelFile() {
        setState { copy(excelSelectedFile = null) }
    }

    private fun selectExcelFile() {
        val fileChooser = FileChooser()
        val selectedExcelFile = fileChooser.selectExcelFile() ?: return

        validateExcelFile(selectedExcelFile)
    }

    private fun validateExcelFile(excelFile: File) {
        WorkbookFactory.create(excelFile) ?: return
        setState {
            copy(
                excelSelectedFile = excelFile
            )
        }
    }

    private fun onFileRenameSuccess(oldFile: PdfListFile, newFile: File) {
        currentState.filesList.find {it == oldFile}?.updateFileAndStatus(newFile = newFile, newStatus = PdfFileStatus.SUCCESS)

        setState {
            copy(
                testStateInt = currentState.testStateInt + 1,
                filesList = currentState.filesList
            )
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