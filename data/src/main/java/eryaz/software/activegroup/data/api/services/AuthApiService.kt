package eryaz.software.activegroup.data.api.services

import eryaz.software.activegroup.data.models.remote.models.PdaVersionModel
import eryaz.software.activegroup.data.models.remote.models.ResultModel
import eryaz.software.activegroup.data.models.remote.request.LoginRequest
import eryaz.software.activegroup.data.models.remote.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/TokenAuth/Authenticate")
    suspend fun login(@Body request: LoginRequest?): ResultModel<UserResponse>

    @GET("api/services/app/Common/GetPdaVersion")
    suspend fun getPdaVersion(): ResultModel<PdaVersionModel>

}