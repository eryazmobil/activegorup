package eryaz.software.activegroup.data.models.dto

data class ProductSpecialShelfDto(
    val product: ProductDto,
    val shelfDto: ShelfDto?,
    val quantity: String
)
