package eryaz.software.activegroup.data.models.dto

data class BarcodeDto(
    val id: Int,
    val product: ProductDto,
    val code: String,
    val quantity: Int
)
