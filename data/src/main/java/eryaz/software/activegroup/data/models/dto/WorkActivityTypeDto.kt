package eryaz.software.activegroup.data.models.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkActivityTypeDto(
    val id: Int,
    val code: String,
    val definition: String
):Parcelable
