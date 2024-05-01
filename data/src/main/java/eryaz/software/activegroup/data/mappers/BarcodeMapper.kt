package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.BarcodeDto
import eryaz.software.activegroup.data.models.remote.response.BarcodeResponse

fun BarcodeResponse.toDto() = BarcodeDto(
    id = id,
    product = product.toDto(),
    code = code,
    quantity = quantity
)