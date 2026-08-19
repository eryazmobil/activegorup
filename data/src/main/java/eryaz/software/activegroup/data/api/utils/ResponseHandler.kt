package eryaz.software.activegroup.data.api.utils

import com.google.gson.Gson
import com.google.gson.JsonParser
import eryaz.software.activegroup.data.models.remote.response.BaseResponse
import eryaz.software.activegroup.data.models.remote.response.ErrorResponse
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber

object ResponseHandler {

    fun <T : Any> handleSuccess(baseResponse: BaseResponse, data: T?): Resource<T> {

        return if (isSuccessful(baseResponse) && data != null) {
            Resource.Success(data)
        } else {
            Resource.Error(
                message = ""
            )
        }
    }

    fun <T : Any> handleException(e: Exception): Resource<T> {
        Timber.e(e)

        if (e is HttpException) {
            return handleErrorBody(body = e.response()?.errorBody()).copy()
        }

        return Resource.Error(
            message = "",
        )
    }

    private fun handleErrorBody(body: ResponseBody?): Resource.Error {
        val raw = try {
            body?.string()
        } catch (e: Exception) {
            null
        }

        val abpMessage = raw?.let {
            try {
                Gson().fromJson(JsonParser.parseString(it), ErrorResponse::class.java)?.error?.message
            } catch (e: Exception) {
                null
            }
        }

        if (!abpMessage.isNullOrBlank()) {
            return Resource.Error(message = abpMessage)
        }
        val fallbackMessage = raw?.let { extractFallbackMessage(it) }

        return Resource.Error(message = fallbackMessage.orEmpty())
    }

    private fun extractFallbackMessage(raw: String): String? {
        if (raw.isBlank()) return null

        return try {
            val json = JsonParser.parseString(raw).asJsonObject

            listOf("detail", "Detail", "message", "Message", "title", "Title").forEach { key ->
                json.get(key)?.takeIf { !it.isJsonNull }?.asString?.let { return it }
            }

            raw
        } catch (e: Exception) {
            raw
        }
    }


    private fun isSuccessful(baseResponse: BaseResponse): Boolean {

        return baseResponse.success
    }
}
