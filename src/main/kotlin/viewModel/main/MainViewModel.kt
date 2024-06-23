package viewModel.main

import data.main.PdfFileStatus
import data.main.PdfListFile
import extensions.onError
import extensions.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import operations.FileChooser
import operations.excel.ExcelHandler
import org.apache.poi.ss.usermodel.WorkbookFactory
import ui.main.MainContract.Event
import ui.main.MainContract.Event.*
import ui.main.MainContract.State
import ui.sidePanel.SidePanelItem
import useCase.AnalyzePdfKteoUseCase
import useCase.AnalyzePdfVehicleIdUseCase
import useCase.RenamePdfUseCase
import viewModel.core.CoreViewModel
import java.awt.Desktop
import java.io.File

class MainViewModel(
    private val coroutineScope: CoroutineScope,
    private val pdfVehicleIdUseCase: AnalyzePdfVehicleIdUseCase,
    private val pdfKteoUseCase: AnalyzePdfKteoUseCase,
    private val renamePdfUseCase: RenamePdfUseCase,
) : CoreViewModel<Event, State>(
    initialState = State(),
    coroutineScope = coroutineScope
) {


    override suspend fun handleEvent(event: Event) {

        when (event) {
            is AnalyzePDFVehicleId -> {
                startTelhKykloforiasAction(event.selectedfiles)
            }
            is RenameTrafficFeesFiles -> {
                renamePdfTrafficFeesFiles(event.selectedfiles)
            }
            is AnalyzePDFKteo -> {
                analyzePdfKteo(event.selectedfiles)
            }
            is SelectFiles -> {
                selectFiles()
            }
            is OpenFileExplorer -> {
                openFileExplorer(event.file.file)
            }
            is SelectExcelFile -> {
                selectExcelFile()
            }
            is ClearSelectedExcelFile -> {
                clearSelectedExcelFile()
            }
            is ClearSelectedPdfFiles -> {
                clearSelectedPdfFiles()
            }
            is SideMenuItemSelected -> {
                sideMenuSelected(event.menuItem)
            }
            is StartTestExecution -> {
                startTestExecution()
            }
        }
    }

    private fun sideMenuSelected(menuSelected: SidePanelItem) {
        setState {
            copy(
                sidePanelItem = menuSelected
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

    private fun selectExcelFile() {
        val fileChooser = FileChooser()
        val selectedExcelFile = fileChooser.selectExcelFile() ?: return

        validateExcelFile(selectedExcelFile)
    }

    private fun clearSelectedExcelFile() {
        setState { copy(excelSelectedFile = null) }
    }

    private fun clearSelectedPdfFiles() {
        setState { copy(filesList = emptyList()) }
    }

    private fun validateExcelFile(excelFile: File) {
        WorkbookFactory.create(excelFile) ?: return
        setState {
            copy(
                excelSelectedFile = excelFile
            )
        }
    }

    private fun startTelhKykloforiasAction(selectedFiles: List<PdfListFile>) {
        if (selectedFiles.isEmpty())
            return

        val excelHandler = currentState.excelSelectedFile?.let { ExcelHandler.initExcelHandler(it) } ?: return

        coroutineScope.launch(Dispatchers.Default) {
            selectedFiles.forEach { selectedFile ->
                renameAndWriteToExcelTrafficFeesFile(selectedFile, excelHandler)
            }
        }
    }

    private suspend fun renameAndWriteToExcelTrafficFeesFile(workingFile: PdfListFile, excelHandler: ExcelHandler) {
        setStateToFile(workingFile, PdfFileStatus.LOADING)
        delay(1)

        val vehicleId = pdfVehicleIdUseCase.invoke(workingFile.file)

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

    private fun renamePdfTrafficFeesFiles(selectedFiles: List<PdfListFile>) {
        if (selectedFiles.isEmpty())
            return

        coroutineScope.launch((Dispatchers.Default)) {
            selectedFiles.forEach { selectedFile ->
                setStateToFile(selectedFile, PdfFileStatus.LOADING)
                delay(1)
                val vehicleId = pdfVehicleIdUseCase.invoke(selectedFile.file)
                if (vehicleId != null) {
                    val renamedFile = renamePdfUseCase.invoke(selectedFile.file, vehicleId)
                    if (renamedFile != null) {
                        onFileRenameSuccess(selectedFile, renamedFile)
                    } else {
                        setStateToFile(selectedFile, PdfFileStatus.FAILED)
                    }
                } else {
                    setStateToFile(selectedFile, PdfFileStatus.FAILED)
                }
            }
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

    private fun analyzePdfKteo(selectedFiles: List<PdfListFile>) {
        if (selectedFiles.isEmpty())
            return

        val excelHandler = currentState.excelSelectedFile?.let { ExcelHandler.initExcelHandler(it) } ?: return

        coroutineScope.launch((Dispatchers.Default)) {
            selectedFiles.forEach { selectedFile ->
                val kteoExtractedDate = pdfKteoUseCase.invoke(selectedFile.file)
                if (kteoExtractedDate != null) {
                    println("File: ${selectedFile.file.name} found Kteo date: $kteoExtractedDate")
                } else {
                    println("Kteo failed to locate date")
                }
            }
        }
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

    private fun startTestExecution() {
        coroutineScope.launch {
        for (i in 1..10) {
            delay(500)
            setState {
                copy(
                    testStateInt = currentState.testStateInt + 1
                )
            }
        }

        }
    }

}