package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.VehiclePackageDto
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VehicleUpVM(
    val repo: OrderRepo,
    val vehicleID: Int,
    val driverId: Int
) : BaseViewModel() {

    private val _packageList = MutableStateFlow(listOf<VehiclePackageDto>())
    val packageList = _packageList.asStateFlow()

    val packageCode = MutableStateFlow("")
    val deliveredNameTxt = MutableStateFlow("")
    val vehicleDownSuccess = MutableStateFlow(false)
    val finishProcess = MutableStateFlow(false)
    val vehicleFinished = MutableStateFlow(false)

    init {
        getOrderHeaderRouteList()
    }

    fun updateOrderHeaderRouteFinish() {
        executeInBackground {
            repo.updateOrderHeaderRouteFinish(shippingRouteId = driverId)
                .onSuccess {
                    vehicleFinished.emit(true)
                }.onError { message, _ ->
                    ErrorDialogDto(
                        title = stringProvider.invoke(R.string.error),
                        message = message
                    )
                }.also {
                    packageCode.emit("")
                    deliveredNameTxt.emit("")
                }
        }
    }

    fun readOrderPackage() = executeInBackground {
        repo.updateOrderHeaderRoute(
            code = packageCode.value,
            shippingRouteId = driverId,
            routeType = 2
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
                routeType = 1
            ).onSuccess {
                _packageList.emit(it)

                if (it.isEmpty()) {
                    finishProcess.emit(true)
                }
            }
        }
    }

    fun updateReturnShipmentByOrderHeaderIdForUp(orderHeaderId: Int) {
        executeInBackground(showProgressDialog = true) {
            repo.updateReturnShipmentByOrderHeaderIdForUp(orderHeaderId = orderHeaderId).onSuccess {
                getOrderHeaderRouteList()
            }
        }
    }
}