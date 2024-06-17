package eryaz.software.activegroup.ui.dashboard.settings.appLock

import androidx.lifecycle.viewModelScope
import eryaz.software.activegorup.data.BuildConfig
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.CombinedStateFlow
import eryaz.software.activegroup.util.extensions.DateUtils
import eryaz.software.activegroup.util.extensions.isValidPassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppLockVM : BaseViewModel() {
    val password = MutableStateFlow("")

    private val _buttonText = MutableStateFlow(
        if (SessionManager.appIsLocked) R.string.app_unlock
        else R.string.app_lock
    )
    val buttonText = _buttonText.asStateFlow()

    private val _appLockStatusChanged = MutableSharedFlow<Boolean>()
    val appLockStatusChanged = _appLockStatusChanged.asSharedFlow()

    private val _wrongPassword = MutableSharedFlow<Boolean>()
    val wrongPassword = _wrongPassword.asSharedFlow()

    val verifyEnable = CombinedStateFlow(password) {
        password.value.trim().length >= 3
    }

    fun verify() {
        viewModelScope.launch {
            val time = DateUtils.getCurrentDate("HHmm")

            if (password.value.trim() == "${BuildConfig.ADMIN_PASS}$time") {
                SessionManager.appIsLocked = !SessionManager.appIsLocked

                _appLockStatusChanged.emit(true)
            } else {
                _wrongPassword.emit(true)
            }
        }
    }
}