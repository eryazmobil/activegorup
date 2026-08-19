package eryaz.software.activegroup.data.api.services

import eryaz.software.activegroup.data.models.remote.response.BaseResponse
import retrofit2.http.PUT
import retrofit2.http.Query

interface ClientApiService {

    @PUT("api/services/app/Client/UpdateClientTest")
    suspend fun updateClientTest(@Query("clientId") clientId: Int): BaseResponse

}
