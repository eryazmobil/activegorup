package eryaz.software.activegroup.ui.dashboard.movement.transferStockCorrection.stockType

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.StockTypeDto
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class StockTyeDialogVM : BaseViewModel() {

    private val _stockTypeList = MutableStateFlow(getItemList())
    val stockTypeList = _stockTypeList.asStateFlow()

    private fun getItemList() =
            listOf(
                StockTypeDto(
                    type = 1,
                    titleRes = R.string.add_product_
                ),
                StockTypeDto(
                    type = 2,
                    titleRes = R.string.remove_product
                )
            )
}