package eryaz.software.activegroup.data.models.dto

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.databinding.ObservableField
import eryaz.software.activegroup.data.enums.DashboardDetailPermissionType

data class DashboardDetailItemDto(
    @DrawableRes
    val iconRes:Int,
    @StringRes
    val titleRes: Int,
    var type: DashboardDetailPermissionType,
    var hasPermission: ObservableField<Boolean>
)