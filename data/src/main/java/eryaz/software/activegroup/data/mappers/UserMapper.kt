package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.CurrentUserDto
import eryaz.software.activegroup.data.models.dto.WorkActionDto
import eryaz.software.activegroup.data.models.remote.response.CurrentUserResponse
import eryaz.software.activegroup.data.models.remote.response.WorkActionResponse

fun CurrentUserResponse.toDto() = CurrentUserDto(
    userId = userId,
    fullName = "$name $surname",
    email = emailAddress,
    username = userName,
    companyId = companyId,
    warehouseId = warehouseId
)

fun WorkActionResponse.toDto() = WorkActionDto(
    workActionId = id,
    workActivity = workActivity?.toDto(),
    workActionType = workActionType?.toDto(),
    processUser = processUser?.toDto(),
    isFinished = isFinished
)

