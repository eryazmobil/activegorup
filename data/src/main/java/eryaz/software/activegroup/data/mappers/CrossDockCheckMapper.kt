package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.ControlPointDto
import eryaz.software.activegroup.data.models.dto.CrossDockCheckDto
import eryaz.software.activegroup.data.models.dto.CrossDockDto
import eryaz.software.activegroup.data.models.dto.OrderDetailDto
import eryaz.software.activegroup.data.models.remote.response.ControlPointResponse
import eryaz.software.activegroup.data.models.remote.response.CrossDockCheckResponse
import eryaz.software.activegroup.data.models.remote.response.CrossDockResponse
import eryaz.software.activegroup.data.models.remote.response.OrderDetailResponse

fun CrossDockCheckResponse.toDto() = CrossDockCheckDto(
    id = id,
    quantity = quantity,
    orderHeader = orderHeader.toDto()
)

fun CrossDockResponse.toDto() = CrossDockDto(
    id = id,
    orderDetail = orderDetail.toDto(),
    quantity = quantity,
    isFinished = isFinished,
    quantityNeed = quantityNeed.toString()
)

fun OrderDetailResponse.toDto() = OrderDetailDto(
    id = id,
    orderHeader = orderHeader.toDto(),
    product = product.toDto(),
    quantity = quantity.toString(),
    quantityShipped = quantityShipped.toString(),
    quantityCollected = quantityCollected.toString(),
)

fun ControlPointResponse.toDto() = ControlPointDto(
    id = id,
    code = code.orEmpty(),
    definition = definition.orEmpty()
)