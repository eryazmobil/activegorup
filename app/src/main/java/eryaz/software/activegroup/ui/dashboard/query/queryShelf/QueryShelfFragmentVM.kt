package eryaz.software.activegroup.ui.dashboard.query.queryShelf

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.ProductShelfQuantityDto
import eryaz.software.activegroup.data.models.dto.ProductSpecialShelfDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class QueryShelfFragmentVM(
    private val repo: WorkActivityRepo
) : BaseViewModel() {
    val searchShelf = MutableStateFlow("")

    private val _shelfDetail = MutableStateFlow(false)
    val shelfDetail = _shelfDetail.asStateFlow()

    private val _productList = MutableStateFlow(listOf<ProductShelfQuantityDto>())
    val productList = _productList.asStateFlow()

    private val _varietyShelf = MutableStateFlow(listOf<ProductSpecialShelfDto>())
    val varietyShelf = _varietyShelf.asStateFlow()

    fun getShelfByCode() = executeInBackground(
        _uiState,
        showProgressDialog = true
    ) {
        repo.getShelfByCode(
            code = searchShelf.value.trim(),
            warehouseId = SessionManager.warehouseId,
            storageId = 0
        ).onSuccess {
            getProductShelfQuantityList(it.shelfId)
            getProductVarietyShelfListByShelfId(it.shelfId)
        }.onError { message, _ ->
            _shelfDetail.emit(false)
            showError(
                ErrorDialogDto(
                    title = stringProvider.invoke(R.string.error),
                    message = message
                )
            )
        }
    }

    private fun getProductShelfQuantityList(shelfId: Int) =
        executeInBackground(_uiState, showProgressDialog = true) {

            repo.getProductShelfQuantityList(
                productId = 0,
                warehouseId = SessionManager.warehouseId,
                storageId = 0,
                companyId = SessionManager.companyId,
                shelfId = shelfId
            ).onSuccess {
                _shelfDetail.emit(true)
                _productList.emit(it)
            }
        }

    private fun getProductVarietyShelfListByShelfId(id: Int) =
        executeInBackground(_uiState, showProgressDialog = true) {
            repo.getProductVarietyShelfListByShelfId(
                shelfId = id
            ).onSuccess {
                _varietyShelf.emit(it)
            }
        }
}