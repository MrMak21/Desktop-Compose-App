package viewModel.main

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import operations.FileChooser
import ui.main.MainContract
import useCase.AnalyzePdfUseCase
import viewModel.core.CoreViewModel
import java.io.File

class MainViewModel(
    private val coroutineScope: CoroutineScope,
    private val pdfVehicleIdUseCase: AnalyzePdfUseCase
) : CoreViewModel<MainContract.Event, MainContract.State>(
    MainContract.State(),
    coroutineScope = coroutineScope
) {


    override suspend fun handleEvent(event: MainContract.Event) {

        when (event) {
            is MainContract.Event.AnalyzePDF -> {
                analyzePdf(event.selectedfiles)
            }
            is MainContract.Event.SelectFiles -> {
                selectFiles()
            }
            is MainContract.Event.TestEventState -> {
                setState {
                    copy(
                        testInt = testInt + 1
                    )
                }
            }
        }
    }

    private fun selectFiles() {
        val fileChooser = FileChooser()
        val selectedFiles = fileChooser.selectMutliplePDF()
        setState {
            copy(
                selectedfiles = selectedFiles
            )
        }
    }

    private fun analyzePdf(selectedFiles: List<File>) {
        if (selectedFiles.isEmpty())
            return


        coroutineScope.launch {
            selectedFiles.forEach {
                val vehicleId = pdfVehicleIdUseCase.invoke(it)
                println("Analyze file: ${it.name} . Found id: ${vehicleId}")
                vehicleId?.let { vehicleId ->
                    val newFile = File(it.parent, "$vehicleId.pdf")
                    val successRename = it.renameTo(newFile)
                    println("Rename file from ${it.name} to $successRename")
                }
            }
            setState {
                copy(
                    mainState = MainContract.MainScreenState.Success(true)
                )
            }
        }
    }
}