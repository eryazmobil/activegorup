package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf

import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.StockTakingDetailDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.CountingRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AssignedShelfVM(
    val repo: CountingRepo,
    val stHeaderId: Int
) : BaseViewModel() {

    private val _shelfList = MutableStateFlow<List<StockTakingDetailDto>>(emptyList())
    val shelfList = _shelfList.asStateFlow()

    init {
        getAllAssignedShelvesToUserForPda()
    }

    fun getAllAssignedShelvesToUserForPda() =
        executeInBackground(_uiState) {
            repo.getAllAssignedShelvesToUserForPda(
                stHeaderId = stHeaderId,
                userId = SessionManager.userId
            ).onSuccess {
                _shelfList.emit(it)
            }
        }
}