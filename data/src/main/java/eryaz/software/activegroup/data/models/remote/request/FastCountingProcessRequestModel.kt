package eryaz.software.activegroup.data.models.remote.request

import com.google.gson.annotations.SerializedName
import eryaz.software.activegroup.data.models.remote.response.ProductQuantityResponse

data class FastCountingProcessRequestModel(
    @SerializedName("shelfId")
    val shelfId: Int,
    @SerializedName("productQuantityDtoList")
    val productQuantityDtoList: List<ProductQuantityResponse>,
    @SerializedName("stockTakingDetailId")
    val stockTakingDetailId: Int
)