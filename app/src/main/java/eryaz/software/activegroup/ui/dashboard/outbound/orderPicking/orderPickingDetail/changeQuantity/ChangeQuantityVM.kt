package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.changeQuantity

import androidx.lifecycle.viewModelScope
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.extensions.toIntOrZero
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChangeQuantityVM(
    private val orderRepo: OrderRepo,
    val orderDetailId: Int,
    val orderQuantity: Int
) : BaseViewModel() {

    private val _navigateToOrder = MutableSharedFlow<Boolean>()
    val navigateToOrder = _navigateToOrder.asSharedFlow()

    val amount = MutableStateFlow("")
    val calculatedAmount = MutableStateFlow("")

    fun updateQuantity() = executeInBackground {
        orderRepo.updateOrderDetailQuantityForPda(
            orderDetailId = orderDetailId,
            quantity = calculatedAmount.value.toIntOrZero()
        ).onSuccess {
            _navigateToOrder.emit(true)
        }
    }

    fun setAmount(amount: String) {
        viewModelScope.launch {
            calculatedAmount.emit(calculateProductQuantity(amount.toIntOrZero()).toString())
        }
    }

    private fun calculateProductQuantity(multiplier: Int): Int {
        if (multiplier == 0) {
            return 0
        }
        return when {
            orderQuantity > multiplier -> {
                val quantityPart = orderQuantity / multiplier
                if (orderQuantity % multiplier == 0) {
                    quantityPart * multiplier
                } else {
                    (quantityPart + 1) * multiplier
                }
            }

            orderQuantity == multiplier -> orderQuantity
            else -> multiplier
        }
    }

}