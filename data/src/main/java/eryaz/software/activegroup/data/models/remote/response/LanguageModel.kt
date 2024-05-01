package eryaz.software.activegroup.data.models.remote.response

import androidx.databinding.ObservableField
import eryaz.software.activegroup.data.enums.LanguageType

data class LanguageModel(
    val lang: LanguageType,
    val isSelected: ObservableField<Boolean>
)