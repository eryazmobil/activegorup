package eryaz.software.activegroup.data.models.remote.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("userNameOrEmailAddress")
    val email: String?,
    @SerializedName("password")
    val password: String?,
    @SerializedName("rememberClient")
    val rememberClient: Boolean = false
)