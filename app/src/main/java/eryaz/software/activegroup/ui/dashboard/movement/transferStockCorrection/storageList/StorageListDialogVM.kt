package eryaz.software.activegroup.ui.dashboard.movement.transferStockCorrection.storageList

import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.StorageDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.UserRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StorageListDialogVM(
    private val userRepo: UserRepo,
) : BaseViewModel() {

    private val _storageList = MutableStateFlow(listOf<StorageDto>())
    val storageList = _storageList.asStateFlow()

    init {
        getStorageListByWarehouse()
    }

    fun getStorageListByWarehouse() = executeInBackground(_uiState) {
        userRepo.getStorageListByWarehouse(
            warehouseId = SessionManager.warehouseId
        ).onSuccess {
            _storageList.emit(it)
        }
    }
}