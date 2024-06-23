package viewModel.core

import data.main.PdfFileStatus
import data.main.PdfListFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ui.common.UiEvent
import ui.common.UiState
import java.awt.Desktop
import java.io.File

abstract class CoreViewModel<E: UiEvent, S: UiState>(
    private val initialState: S,
    private val coroutineScope: CoroutineScope
) {

    val currentState: S
        get() = state.value

    private val state: MutableStateFlow<S> = MutableStateFlow(initialState)

    fun state(): StateFlow<S> = state

    protected fun setState(reduce: S.() -> S) {
        val prevState = currentState
        val newState = prevState.reduce()
        state.value = newState
    }

    fun onEvent(event: E) = coroutineScope.launch {
        handleEvent(event)
    }

    protected abstract suspend fun handleEvent(event: E)

    protected abstract fun setStateToFile(file: PdfListFile, newFileStatus: PdfFileStatus)

    protected fun openFileExplorer(file: File) {
        Desktop.getDesktop().open(file.parentFile)
    }

}