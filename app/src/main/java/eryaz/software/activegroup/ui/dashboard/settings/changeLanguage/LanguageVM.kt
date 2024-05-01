package eryaz.software.activegroup.ui.dashboard.settings.changeLanguage

import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import eryaz.software.activegroup.data.enums.LanguageType
import eryaz.software.activegroup.data.models.remote.response.LanguageModel
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LanguageVM : BaseViewModel() {

    private val _langList = MutableStateFlow<List<LanguageModel>>(emptyList())
    val langList = _langList.asStateFlow()

    init {
        getLangList()
    }

    private fun getLangList() {
        viewModelScope.launch {
            val model = LanguageType.values().map {
                LanguageModel(
                    lang = it,
                    isSelected = ObservableField(it == SessionManager.language)
                )
            }

            _langList.emit(model)
        }
    }
}
