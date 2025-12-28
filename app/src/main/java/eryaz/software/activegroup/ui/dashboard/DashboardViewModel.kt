package eryaz.software.activegroup.ui.dashboard

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.DashboardItemDto
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.enums.DashboardPermissionType
import eryaz.software.activegroup.data.models.dto.CurrentUserDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.UserRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class DashboardViewModel(private val repo: UserRepo) : BaseViewModel() {
    private val _dashboardItemList = MutableLiveData<List<DashboardItemDto>>(emptyList())
    val dashboardItemList: LiveData<List<DashboardItemDto>> = _dashboardItemList

    private val _currentUserDto = MutableStateFlow<CurrentUserDto?>(null)

    init {
        getCurrentLoginInformations()
        getCurrentLoginHasPermissionsForPDAMenu()
    }

    private fun getCurrentLoginHasPermissionsForPDAMenu() =
        executeInBackground(_uiState) {
            repo.getCurrentLoginHasPermissionsForPDAMenu().onSuccess { originalList ->

                val permissionList = originalList.toMutableList().apply {
                    if (size == 2 && contains("Pages.PDA.Outbound.Delivery") && contains("Pages.PDA.Outbound")) {
                        remove("Pages.PDA.Outbound")
                    }
                }

                val menuPool = listOf(
                    DashboardItemDto(R.drawable.inbound, R.string.main_menu_item_1, DashboardPermissionType.INBOUND, ObservableField(true)),
                    DashboardItemDto(R.drawable.outbound, R.string.main_menu_item_2, DashboardPermissionType.OUTBOUND, ObservableField(true)),
                    DashboardItemDto(R.drawable.delivery, R.string.main_menu_item_3, DashboardPermissionType.MOVEMENT, ObservableField(true)),
                    DashboardItemDto(R.drawable.recording, R.string.main_menu_item_4, DashboardPermissionType.RECORDING, ObservableField(true)),
                    DashboardItemDto(R.drawable.return_icon, R.string.main_menu_item_5, DashboardPermissionType.RETURNING, ObservableField(true)),
                    DashboardItemDto(R.drawable.counting, R.string.main_menu_item_6, DashboardPermissionType.COUNTING, ObservableField(true)),
                    DashboardItemDto(R.drawable.smart_factory, R.string.main_menu_item_7, DashboardPermissionType.PRODUCTION, ObservableField(true)),
                    DashboardItemDto(R.drawable.query, R.string.main_menu_item_8, DashboardPermissionType.QUERY, ObservableField(true)),
                    DashboardItemDto(R.drawable.delivery, R.string.main_menu_item_3, DashboardPermissionType.D2D, ObservableField(true))
                )

                val authorizedItems = menuPool.filter { item ->
                    permissionList.contains(item.type.permission)
                }

                _dashboardItemList.value = authorizedItems
            }
        }

    private fun getCurrentLoginInformations() =
        executeInBackground {
            repo.getCurrentLoginInformations().onSuccess {
                _currentUserDto.emit(it)

                SessionManager.companyId = it.companyId ?: 0
                SessionManager.warehouseId = it.warehouseId ?: 0
                SessionManager.userId = it.userId
            }
        }
}