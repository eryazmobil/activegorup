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

    private val _packageList = MutableStateFlow(listOf<VehiclePackageDto>())
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

    fun readOrderPackage() = executeInBackground {
        repo.updateOrderHeaderRoute(
            code = packageCode.value,
            shippingRouteId = driverId,
            routeType = 1
        ).onSuccess {
            getOrderHeaderRouteList()
            packageCode.emit("")
            vehicleDownSuccess.emit(true)
        }.onError { _, _ ->
            packageCode.emit("")
        }
    }

    fun getOrderHeaderRouteList() {
        executeInBackground(showProgressDialog = true) {
            repo.getOrderHeaderRouteList(
                shippingRouteId = driverId,
                routeType = 3
            ).onSuccess {
                _packageList.emit(it)

                if (it.isEmpty()) {
                    finishProcess.emit(true)
                }
            }
        }
    }

    fun deleteOrderHeaderRouteByOrderHeaderIdForDown(orderHeaderId:Int) {
        executeInBackground(showProgressDialog = true) {
            repo.deleteOrderHeaderRouteByOrderHeaderIdForDown(orderHeaderId = orderHeaderId).onSuccess {
                getOrderHeaderRouteList()
            }
        }
    }
}