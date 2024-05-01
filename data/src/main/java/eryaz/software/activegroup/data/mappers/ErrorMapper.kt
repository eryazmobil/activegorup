package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.ErrorDto
import eryaz.software.activegroup.data.models.remote.response.ErrorModel

fun ErrorModel.toDto() = ErrorDto(
    code = code,
    message = message
)