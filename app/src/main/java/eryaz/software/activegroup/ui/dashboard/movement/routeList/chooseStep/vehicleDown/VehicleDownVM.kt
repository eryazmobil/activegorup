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
    val routeID: Int
) : BaseViewModel() {

    private val _packageList = MutableStateFlow(listOf<VehiclePackageDto>())
    val packageList = _packageList.asStateFlow()

    val packageCode = MutableStateFlow("")
    val vehicleDownSuccess = MutableStateFlow(false)

    init {
        getOrderHeaderRouteList()
    }

    fun createOrderHeaderRoute() {
        executeInBackground {
            repo.createOrderHeaderRoute(
                code = packageCode.value,
                routeType = vehicleID,
                shippingRouteId = routeID,
            ).onSuccess {
                vehicleDownSuccess.emit(true)
                getOrderHeaderRouteList()
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

    fun getOrderHeaderRouteList() {
        executeInBackground(showProgressDialog = true) {
            repo.getOrderHeaderRouteList(
                shippingRouteId = routeID
            ).onSuccess {
                if (it.isNotEmpty()) {
                    _packageList.emit(it)
                }
            }
        }
    }
}