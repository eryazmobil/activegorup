package eryaz.software.activegroup.data.models.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkActivityDto(
    val workActivityId: Int,
    val workActivityCode: String,
    val workActivityType: WorkActivityTypeDto?,
    val client: ClientDto?,
    val creationTime: String,
    val isLocked: Boolean,
    val company: CompanyDto?,
    val shippingType: ShippingTypeDto?,
    val notes: String,
    val controlPointDefinition: String?,
    val hasPriority: Boolean
):Parcelable