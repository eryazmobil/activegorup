package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.PdaVersionDto
import eryaz.software.activegroup.data.models.remote.models.PdaVersionModel

fun PdaVersionModel.toDto() = PdaVersionDto(
    version = version,
    downloadLink = downloadLink,
    id = id,
    apkZipName = downloadLink?.substringAfterLast("/"),
    apkFileName = downloadLink?.substringAfterLast("/")?.substringBeforeLast(".")
)
