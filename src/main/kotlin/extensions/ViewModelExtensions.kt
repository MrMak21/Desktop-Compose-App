package extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import ui.common.UiEvent
import ui.common.UiState
import viewModel.core.CoreViewModel

@Composable
fun <E: UiEvent, S: UiState> CoreViewModel<E, S>.startCollecting(): State<S> {
    return state().collectAsState()
}