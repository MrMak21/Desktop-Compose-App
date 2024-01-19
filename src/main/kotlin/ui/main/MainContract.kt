package ui.main

import ui.common.UiEvent
import ui.common.UiState
import java.io.File

interface MainContract {

    sealed class Event: UiEvent {

        data class AnalyzePDF(val selectedfiles: List<File>): Event()
        object TestEventState: Event()
        object SelectFiles: Event()
    }

    data class State(
        val mainState: MainScreenState = MainScreenState.None,
        var testInt: Int = 0,
        val selectedfiles: List<File> = emptyList()
    ): UiState

    sealed class MainScreenState {
        object None: MainScreenState()
        object Loading: MainScreenState()
        data class Error(val error: Throwable): MainScreenState()
        data class Success(val isSuccess: Boolean): MainScreenState()
    }
}