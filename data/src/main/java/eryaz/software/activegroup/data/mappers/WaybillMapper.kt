package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.WaybillListDetailDto
import eryaz.software.activegroup.data.models.remote.response.WaybillListDetailResponse

fun WaybillListDetailResponse.toDto() = WaybillListDetailDto(
    product = product.toDto(),
    quantity = quantity,
    quantityOrder = quantityOrder,
    quantityPlaced = quantityPlaced,
    quantityControlled = quantityControlled,
    quantityControlledParameter = quantityControlled.toString(),
    id = id
)