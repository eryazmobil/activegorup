package eryaz.software.activegroup.data.models.remote.response

import com.google.gson.annotations.SerializedName

data class ShippingRouteResponse(
    @SerializedName("code")
    val code :String
)
