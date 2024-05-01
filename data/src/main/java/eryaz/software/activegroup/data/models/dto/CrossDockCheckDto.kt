package eryaz.software.activegroup.data.models.dto


data class CrossDockCheckDto(
    val id: Int,
    val quantity: Int,
    val orderHeader: OrderHeaderDto
)
