package eryaz.software.activegroup.data.mappers

import eryaz.software.activegroup.data.models.dto.StockTackingProcessDto
import eryaz.software.activegroup.data.models.dto.StockTakingDetailDto
import eryaz.software.activegroup.data.models.dto.StockTakingHeaderDto
import eryaz.software.activegroup.data.models.remote.response.StockTackingProcessResponse
import eryaz.software.activegroup.data.models.remote.response.StockTakingDetailResponse
import eryaz.software.activegroup.data.models.remote.response.StockTakingHeaderResponse

fun StockTakingHeaderResponse.toDto() = StockTakingHeaderDto(
    id = id,
    workActivity = workActivity?.toDto(),
    stockTakingType = stockTakingType.toDto()
)

fun StockTackingProcessResponse.toDto() = StockTackingProcessDto(
    id = id,
    stockTakingDetail = stockTakingDetail?.toDto(),
    shelf = shelf?.toDto(),
    shelfCurrentQuantity = shelfCurrentQuantity,
    editedShelfCurrentQuantity = shelfCurrentQuantity,
    productDto = product?.toDto()
)

fun StockTakingDetailResponse.toDto() = StockTakingDetailDto(
    id = id,
    stockTakingHeader = stockTakingHeader?.toDto(),
    stockTakingType = stockTakingType?.toDto(),
    shelfDto = shelfResponse.toDto(),
    isStarted = isStarted,
    isFinished = isFinished
)