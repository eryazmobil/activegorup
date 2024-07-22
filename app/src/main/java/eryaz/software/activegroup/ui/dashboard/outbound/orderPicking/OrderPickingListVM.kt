package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.enums.ActionType
import eryaz.software.activegroup.data.models.dto.WarningDialogDto
import eryaz.software.activegroup.data.models.dto.WorkActivityDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.persistence.TemporaryCashManager
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.extensions.orZero
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class OrderPickingListVM(
    val repo: WorkActivityRepo
) : BaseViewModel() {

    val search = MutableLiveData("")

    private val _navigateToDetail = MutableSharedFlow<Boolean>()
    val navigateToDetail = _navigateToDetail.asSharedFlow()

    private val _orderPickingList = MutableLiveData<List<WorkActivityDto?>>(emptyList())
    val orderPickingList: LiveData<List<WorkActivityDto?>> = _orderPickingList

    fun searchList() = search.switchMap { query ->
        MutableLiveData<List<WorkActivityDto?>>().apply {
            value = filterData(query)
        }
    }

    private fun filterData(query: String): List<WorkActivityDto?> {
        val dataList = _orderPickingList.value.orEmpty()

        val filteredList = dataList.filter { data ->
            data?.notes?.contains(query, ignoreCase = true) ?: true
        }
        return filteredList
    }

    fun getActiveWorkAction() =
        executeInBackground(showErrorDialog = false) {
            repo.getWorkActionActive(
                companyId = SessionManager.companyId,
                warehouseId = SessionManager.warehouseId,
                workActivityType = WORK_ACTIVITY_TYPE_SHIPPING,
                workActionType = WORK_ACTION_TYPE_PICKING
            ).onSuccess {
                _navigateToDetail.emit(true)
                TemporaryCashManager.getInstance().workAction = it
                TemporaryCashManager.getInstance().workActivity = it.workActivity
            }.onError { _, _ ->
                _navigateToDetail.emit(false)
                getShippingWorkActivityList()
            }
        }

    fun getShippingWorkActivityList() {
        executeInBackground(_uiState) {
            repo.getShippingWorkActivityList(
                companyId = SessionManager.companyId,
                warehouseId = SessionManager.warehouseId,
                userId = SessionManager.userId
            ).onSuccess {
                if (it.isEmpty()) {
                    _orderPickingList.value = emptyList()
                    showWarning(
                        WarningDialogDto(
                            title = stringProvider.invoke(R.string.not_found_work_activity),
                            message = stringProvider.invoke(R.string.list_is_empty)
                        )
                    )
                }
                _orderPickingList.value = it
            }.onError { _, _ ->
                _orderPickingList.value = emptyList()
            }
        }
    }

    fun getWorkActionForPda() {
        executeInBackground(showProgressDialog = true) {

            repo.getWorkActionForPda(
                userId = SessionManager.userId,
                workActivityId = TemporaryCashManager.getInstance().workActivity?.workActivityId.orZero(),
                actionTypeId = TemporaryCashManager.getInstance().workActionTypeList?.find { model -> model.code == "Picking" }?.id.orZero()
            ).onSuccess {
                TemporaryCashManager.getInstance().workAction = it
                _navigateToDetail.emit(true)
            }.onError { _, _ ->
                createWorkAction()
            }
        }
    }

    private fun createWorkAction() {
        executeInBackground(showProgressDialog = true) {
            repo.createWorkAction(
                activityId = TemporaryCashManager.getInstance().workActivity?.workActivityId ?: 0,
                actionTypeCode = WORK_ACTION_TYPE_PICKING
            ).onSuccess {
                TemporaryCashManager.getInstance().workAction = it
                _navigateToDetail.emit(true)
            }
        }
    }

    companion object {
        const val WORK_ACTIVITY_TYPE_SHIPPING = "Shipping"
        const val WORK_ACTION_TYPE_PICKING = "Picking"
    }
}