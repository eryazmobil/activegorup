package eryaz.software.activegroup.ui.dashboard.settings.idempotencyTest

import androidx.lifecycle.viewModelScope
import eryaz.software.activegroup.data.api.interceptors.IdempotencyKeyInterceptor
import eryaz.software.activegroup.data.api.utils.Resource
import eryaz.software.activegroup.data.repositories.ClientRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

data class IdempotencyLogLine(
    val index: Int,
    val timestamp: String,
    val idempotencyKey: String,
    val resultText: String,
    val isError: Boolean = false,
    val errorMessage: String? = null
)

class IdempotencyTestVM(private val repo: ClientRepo) : BaseViewModel() {

    private val _logLines = MutableSharedFlow<IdempotencyLogLine>(extraBufferCapacity = 512)
    val logLines = _logLines.asSharedFlow()

    private val _isRunning = MutableStateFlow(false)
    val isRunning = _isRunning.asStateFlow()

    private var loopJob: Job? = null
    private var requestIndex = 0

    @Volatile
    private var lastKeySeen: String = "-"

    private val timeFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    fun start(clientId: Int, intervalMillis: Long) {
        if (_isRunning.value) return

        _isRunning.value = true
        requestIndex = 0

        IdempotencyKeyInterceptor.listener = { _, key -> lastKeySeen = key }

        loopJob = viewModelScope.launch {
            while (isActive) {
                requestIndex++
                val currentIndex = requestIndex

                val result = repo.updateClientTest(clientId)
                val resultText = when (result) {
                    is Resource.Success -> "OK (success=${result.data})"
                    is Resource.Error -> "HATA: ${result.message}"
                }

                _logLines.emit(
                    IdempotencyLogLine(
                        index = currentIndex,
                        timestamp = timeFormat.format(System.currentTimeMillis()),
                        idempotencyKey = lastKeySeen,
                        resultText = resultText,
                        isError = result is Resource.Error,
                        errorMessage = (result as? Resource.Error)?.message
                    )
                )

                delay(intervalMillis)
            }
        }
    }

    fun stop() {
        loopJob?.cancel()
        loopJob = null
        _isRunning.value = false
        IdempotencyKeyInterceptor.listener = null
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}
