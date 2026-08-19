package eryaz.software.activegroup.data.repositories

import eryaz.software.activegroup.data.api.services.ClientApiService
import eryaz.software.activegroup.data.api.utils.ResponseHandler

class ClientRepo(private val api: ClientApiService) : BaseRepo() {

    suspend fun updateClientTest(clientId: Int) = callApi {
        val response = api.updateClientTest(clientId)
        ResponseHandler.handleSuccess(response, response.success)
    }

}
