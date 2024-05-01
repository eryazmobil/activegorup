package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.ClientDto
import eryaz.software.activegroup.data.models.remote.response.ClientSmallResponse

fun ClientSmallResponse.toDto() = ClientDto(
    id = id,
    code = code,
    name = name ?: "Hatalı işlem"
)