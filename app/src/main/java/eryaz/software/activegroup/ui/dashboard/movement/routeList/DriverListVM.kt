package eryaz.software.activegroup.ui.dashboard.movement.driverList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.WarningDialogDto
import eryaz.software.activegroup.data.models.remote.response.DriverResponse
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.ui.base.BaseViewModel

class DriverListVM(private val repo: OrderRepo) : BaseViewModel() {

    private val _driverList = MutableLiveData<List<DriverResponse?>>(emptyList())
    val driverList: LiveData<List<DriverResponse?>> = _driverList

    val search = MutableLiveData("")

    fun searchList() = search.switchMap { query ->
        MutableLiveData<List<DriverResponse?>>().apply {
            value = filterData(query)
        }
    }

    private fun filterData(query: String): List<DriverResponse?> {
        val dataList = _driverList.value.orEmpty()

        val filteredList = dataList.filter { data ->
            data?.code?.contains(query, ignoreCase = true) ?: true
        }
        return filteredList
    }

    fun fetchDriverList() {
        executeInBackground(_uiState) {
            repo.getDriverList(
                SessionManager.warehouseId
            ).onSuccess {
                if (it.isEmpty()) {
                    _driverList.value = emptyList()
                    showWarning(
                        WarningDialogDto(
                            title = stringProvider.invoke(R.string.route_not_founded),
                            message = stringProvider.invoke(R.string.list_is_empty)
                        )
                    )
                }
                _driverList.value = it

            }.onError { _, _ ->
                _driverList.value = emptyList()

            }
        }
    }
}