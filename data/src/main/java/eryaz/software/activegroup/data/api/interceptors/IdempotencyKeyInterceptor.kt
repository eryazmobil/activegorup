package eryaz.software.activegroup.data.api.interceptors

import eryaz.software.activegroup.data.persistence.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.security.MessageDigest

class IdempotencyKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.method !in IDEMPOTENT_METHODS || request.header(HEADER_NAME) != null) {
            return chain.proceed(request)
        }

        val key = sha256Of(request)

        listener?.invoke(request.url.toString(), key)

        val newRequest = request.newBuilder()
            .addHeader(HEADER_NAME, key)
            .build()

        return chain.proceed(newRequest)
    }

    private fun sha256Of(request: okhttp3.Request): String {
        val bodyBytes = try {
            val buffer = Buffer()
            request.body?.writeTo(buffer)
            buffer.readByteArray()
        } catch (e: Exception) {
            ByteArray(0)
        }

        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(SessionManager.userId.toString().toByteArray())
        digest.update(request.method.toByteArray())
        digest.update(request.url.toString().toByteArray())
        digest.update(bodyBytes)

        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    companion object {
        const val HEADER_NAME = "Idempotency-Key"
        private val IDEMPOTENT_METHODS = setOf("POST", "PUT", "PATCH")
        var listener: ((url: String, key: String) -> Unit)? = null
    }
}
