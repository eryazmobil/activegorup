package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.ProductSpecialShelfDto
import eryaz.software.activegroup.data.models.dto.ShelfDto
import eryaz.software.activegroup.data.models.remote.response.ProductSpecialShelfResponse
import eryaz.software.activegroup.data.models.remote.response.ShelfResponse

fun ShelfResponse.toDto() = ShelfDto(
    shelfAddress = fullAddress,
    shelfId = shelfId,
    quantity = quantity.toInt().toString()
)

fun ProductSpecialShelfResponse.toDto() = ProductSpecialShelfDto(
    shelfDto = shelf?.toDto(),
    product = product.toDto(),
    quantity = quantity.toString()
)