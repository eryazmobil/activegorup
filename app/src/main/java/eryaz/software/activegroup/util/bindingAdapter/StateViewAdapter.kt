package eryaz.software.activegroup.util.bindingAdapter

import androidx.databinding.BindingAdapter
import eryaz.software.activegroup.data.enums.UiState
import eryaz.software.activegroup.util.widgets.StateView
import eryaz.software.activegroup.util.widgets.ViewState

@BindingAdapter(value = ["sv_viewState", "isSwipeRefresh"], requireAll = false)
fun StateView.viewState(uiState: UiState?, isSwipeRefresh: Boolean = false) {
    setViewState(
        when (uiState) {
            UiState.SUCCESS -> ViewState.CONTENT
            UiState.LOADING -> if (isSwipeRefresh) getViewState() else ViewState.LOADING
            UiState.ERROR -> ViewState.ERROR
            UiState.EMPTY -> ViewState.EMPTY
            else -> ViewState.CONTENT
        }
    )
}