package eryaz.software.activegroup.data.models.dto

data class ProductShelfQuantityDto(
    val id: Int,
    val product: ProductDto,
    val company: CompanyDto,
    val storage: StorageDto,
    val warehouse: WarehouseDto,
    val shelf: ShelfDto?,
    val quantity: String,
    val storageText:String

)
