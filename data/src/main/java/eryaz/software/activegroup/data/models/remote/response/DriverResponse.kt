package eryaz.software.activegroup.data.models.remote.response

import com.google.gson.annotations.SerializedName

data class DriverResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("id")
    val id: Int
)