package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleDown

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.VehiclePackageDto
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleDownVM(
    val repo: OrderRepo,
    val vehicleID: Int,
    val driverId: Int
) : BaseViewModel() {
    private var isRequestInProgress = false

    private val _packageList = MutableStateFlow<List<VehiclePackageDto>>(emptyList())
    val packageList = _packageList.asStateFlow()

    val packageCode = MutableStateFlow("")
    val finishProcess = MutableStateFlow(false)
    val vehicleDownSuccess = MutableStateFlow(false)
    val vehicleOnTheRoad = MutableStateFlow(false)

    init {
        getOrderHeaderRouteList()
    }

    fun updateOrderHeaderRoadStatus() {
        executeInBackground {
            repo.updateOrderHeaderRoadStatus(
                carStatus = 1,
                shippingRouteId = driverId
            ).onSuccess {
                vehicleOnTheRoad.emit(true)
            }.onError { message, _ ->
                ErrorDialogDto(
                    title = stringProvider.invoke(R.string.error),
                    message = message
                )
            }.also {
                packageCode.emit("")
            }
        }
    }

    fun readOrderPackage(code: String) {

        if (isRequestInProgress) {
            return
        }

        isRequestInProgress = true
        executeInBackground {
            repo.updateOrderHeaderRoute(
                code = code,
                shippingRouteId = driverId,
                routeType = 1
            ).onSuccess {
                packageCode.emit("")
                vehicleDownSuccess.emit(true)

                getOrderHeaderRouteList()
            }.onError { _, _ ->
                packageCode.emit("")
            }.also {
                isRequestInProgress = false
            }
        }
    }

    fun getOrderHeaderRouteList() {
        executeInBackground(showProgressDialog = true) {
            repo.getOrderHeaderRouteList(
                shippingRouteId = driverId,
                routeType = 3
            ).onSuccess {
                _packageList.emit(it)
                finishProcess.emit(it.isNotEmpty() && it.all { list -> list.warehouse != null })

            }.onError { _, _ ->
                _packageList.emit(emptyList())
                finishProcess.emit(true)
            }
        }
    }

    fun deleteOrderHeaderRouteByOrderHeaderIdForDown(orderHeaderId: Int) {
        executeInBackground(showProgressDialog = true) {
            repo.deleteOrderHeaderRouteByOrderHeaderIdForDown(orderHeaderId = orderHeaderId)
                .onSuccess {
                    getOrderHeaderRouteList()
                }
        }
    }
}