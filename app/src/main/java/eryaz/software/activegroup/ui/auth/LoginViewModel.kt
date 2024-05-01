package eryaz.software.activegroup.ui.auth

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.enums.UiState
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.remote.request.LoginRequest
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.persistence.TemporaryCashManager
import eryaz.software.activegroup.data.repositories.AuthRepo
import eryaz.software.activegroup.data.repositories.UserRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.CombinedStateFlow
import eryaz.software.activegroup.util.extensions.isValidPassword
import eryaz.software.activegroup.util.extensions.isValidUserId
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoginViewModel(
    private val authRepo: AuthRepo,
    private val userRepo: UserRepo
) : BaseViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _navigateToMain = MutableSharedFlow<Boolean>()
    val navigateToMain = _navigateToMain.asSharedFlow()

    val loginEnable = CombinedStateFlow(email, password) {
        email.value.isValidUserId() && password.value.isValidPassword()
    }

    init {
        _uiState.value = UiState.EMPTY
    }

    fun login() = executeInBackground(_uiState, hasNextRequest = true) {
        val request = LoginRequest(
            email = email.value,
            password = password.value
        )

        authRepo.login(request).onSuccess {
            SessionManager.token = it.accessToken

            fetchWorkActionTypeList()
        }.onError { message, _ ->
            showError(
                ErrorDialogDto(
                    titleRes = R.string.error,
                    message = message
                )
            )
        }
    }

    private fun fetchWorkActionTypeList() = executeInBackground(_uiState) {
        userRepo.fetchWorkActionTypeList().onSuccess {
            TemporaryCashManager.getInstance().workActionTypeList = it
            _navigateToMain.emit(true)
        }
    }
}



