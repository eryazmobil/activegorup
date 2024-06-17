package eryaz.software.activegroup.data.models.dto


data class WorkActionDto(
    val workActionId: Int,
    val workActivity: WorkActivityDto?,
    val workActionType: WorkActivityTypeDto?,
    val processUser: CurrentUserDto?,
    val isFinished: Boolean
)