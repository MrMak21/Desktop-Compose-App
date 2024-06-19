package viewModel.main

import data.main.PdfFileStatus
import data.main.PdfListFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import operations.FileChooser
import operations.excel.ExcelHandler
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import ui.main.MainContract
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
) : CoreViewModel<MainContract.Event, MainContract.State>(
    MainContract.State(),
    coroutineScope = coroutineScope
) {


    override suspend fun handleEvent(event: MainContract.Event) {

        when (event) {
            is MainContract.Event.AnalyzePDFVehicleId -> {
                analyzePdfVehicleId(event.selectedfiles)
            }
            is MainContract.Event.AnalyzePDFKteo -> {
                analyzePdfKteo(event.selectedfiles)
            }
            is MainContract.Event.SelectFiles -> {
                selectFiles()
            }
            is MainContract.Event.OpenFileExplorer -> {
                openFileExplorer(event.file.file)
            }
            is MainContract.Event.SelectExcelFile -> {
                selectExcelFile()
            }
            is MainContract.Event.ClearSelectedExcelFile -> {
                clearSelectedExcelFile()
            }
            is MainContract.Event.ClearSelectedPdfFiles -> {
                clearSelectedPdfFiles()
            }
            is MainContract.Event.SideMenuItemSelected -> {
                sideMenuSelected(event.menuItem)
            }
            is MainContract.Event.StartTestExecution -> {
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

    private fun openFileExplorer(file: File) {
        Desktop.getDesktop().open(file.parentFile)
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
        val workbook = WorkbookFactory.create(excelFile) ?: return
        setState {
            copy(
                excelSelectedFile = excelFile
            )
        }
        }

    private fun analyzePdfVehicleId(selectedFiles: List<PdfListFile>) {
        if (selectedFiles.isEmpty())
            return


        val excelHandler = currentState.excelSelectedFile?.let { ExcelHandler.initExcelHandler(it) } ?: return

        val errorFilesList: ArrayList<PdfListFile> = arrayListOf()
        val successFilesList: ArrayList<PdfListFile> = arrayListOf()

        coroutineScope.launch {
            selectedFiles.forEach { selectedFile ->
                setStateToFile(selectedFile, PdfFileStatus.LOADING)

                val vehicleId = pdfVehicleIdUseCase.invoke(selectedFile.file)
                if (vehicleId != null) {
                    renamePdfUseCase.invoke(selectedFile.file, vehicleId)
                    excelHandler.writeTelhKykloforiasToExcel(vehicleId)

                    setStateToFile(selectedFile, PdfFileStatus.SUCCESS)
                    successFilesList.add(selectedFile)
                } else {
                    errorFilesList.add(selectedFile)
                    setStateToFile(selectedFile, PdfFileStatus.FAILED)
                }
            }
        }
    }

    private fun analyzePdfKteo(selectedFiles: List<PdfListFile>) {
        if (selectedFiles.isEmpty())
            return

        val excelHandler = currentState.excelSelectedFile?.let { ExcelHandler.initExcelHandler(it) } ?: return

        coroutineScope.launch {
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

    private fun setStateToFile(file: PdfListFile, newFileStatus: PdfFileStatus) {
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