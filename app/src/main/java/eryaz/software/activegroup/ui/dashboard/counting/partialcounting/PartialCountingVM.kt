package eryaz.software.activegroup.ui.dashboard.counting.partialcounting

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.BarcodeDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.CountingRepo
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.CombinedStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PartialCountingVM(
    private val countingRepo: CountingRepo,
    private val workActivityRepo: WorkActivityRepo
) : BaseViewModel() {

    private var productID: Int = 0
    private var stDetailId: Int = 0
    private var stHeaderId: Int = 0

    val searchProduct = MutableStateFlow("")
    val searchShelf = MutableStateFlow("")
    var quantityEdt = MutableStateFlow("")

    private val _productDetail = MutableStateFlow<BarcodeDto?>(null)
    val productDetail = _productDetail.asStateFlow()

    private val _shelfIsValid = MutableStateFlow(false)
    val shelfIsValid = _shelfIsValid.asStateFlow()

    private val _showProductDetail = MutableStateFlow(false)
    val showProductDetail = _showProductDetail.asStateFlow()

    val continueEnable = CombinedStateFlow(quantityEdt) {
        quantityEdt.value.isNotEmpty()
    }


    init {
        getPdaPartialStockTakingWorkActivityAndSTHeader(
            SessionManager.companyId,
            SessionManager.warehouseId
        )
    }

    fun getBarcodeByCode() {
        executeInBackground(
            showErrorDialog = false,
            showProgressDialog = true
        ) {
            workActivityRepo.getBarcodeByCode(
                searchProduct.value.trim(), SessionManager.companyId
            ).onSuccess {
                if (it.product.id == 0) {
                    showError(
                        ErrorDialogDto(
                            titleRes = R.string.error,
                            messageRes = R.string.msg_empty_shelf
                        )
                    )
                } else {
                    _productDetail.emit(it)
                    productID = it.product.id
                    _showProductDetail.emit(true)
                }
            }.onError { _, _ ->
                showError(
                    ErrorDialogDto(
                        title = stringProvider.invoke(R.string.error),
                        message = stringProvider.invoke(R.string.msg_no_barcode)
                    )
                )
                _showProductDetail.emit(false)

            }
        }
    }

    fun getShelfByCode() {
        executeInBackground(
            showErrorDialog = true,
            showProgressDialog = true
        ) {
            workActivityRepo.getShelfByCode(
                code = searchShelf.value.trim(),
                warehouseId = SessionManager.warehouseId,
                storageId = 0
            ).onSuccess {
                _shelfIsValid.emit(true)
                createSTDetailWithUserAndShelvesPartial(
                    stHeaderId,
                    SessionManager.userId,
                    it.shelfId
                )
            }.onError { _, _ ->
                showError(
                    ErrorDialogDto(
                        title = stringProvider.invoke(R.string.error),
                        message = stringProvider.invoke(R.string.msg_shelf_not_found)
                    )
                )
            }
        }
    }

    private fun getPdaPartialStockTakingWorkActivityAndSTHeader(
        companyId: Int,
        warehouseId: Int
    ) {
        executeInBackground(
            showProgressDialog = true,
            showErrorDialog = true
        ) {
            countingRepo.getPdaPartialStockTakingWorkActivityAndSTHeader(
                companyId,
                warehouseId
            ).onSuccess {
                stHeaderId = it
            }
        }
    }

    private fun createSTDetailWithUserAndShelvesPartial(
        stHeaderId: Int,
        assignedUserId: Int,
        shelfId: Int
    ) {
        executeInBackground(
            showErrorDialog = true,
            showProgressDialog = true
        ) {
            countingRepo.createSTDetailWithUserAndShelvesPartial(
                stHeaderId,
                assignedUserId,
                shelfId
            ).onSuccess {
                stDetailId = it
            }
        }
    }

    fun finishPartialStockTacking(whenComplete: () -> Unit) {
        executeInBackground(
            showErrorDialog = true,
            showProgressDialog = true
        ) {
            countingRepo.finishPartialStockTacking(stHeaderId)
                .onSuccess {
                    whenComplete()
                }
        }
    }

    fun nextPartialStockTackingDetail(whenComplete: () -> Unit) {
        executeInBackground(
            showErrorDialog = true,
            showProgressDialog = true
        ) {
            countingRepo.nextPartialStockTackingDetail(stDetailId)
                .onSuccess {
                    afterCounting(true)
                    whenComplete()
                }
        }
    }

    fun createSTActionProcessFromPartialStockTaking() {

        executeInBackground(showErrorDialog = true, showProgressDialog = true) {
            val quantity = quantityEdt.value.toInt()
            countingRepo.createSTActionProcessFromPartialStockTaking(
                stHeaderId,
                stDetailId,
                productID,
                quantity
            ).onSuccess {
                afterCounting(false)
            }
        }
    }

    private suspend fun afterCounting(isNewShelf: Boolean) {
        if (isNewShelf) {
            _showProductDetail.emit(false)
            _shelfIsValid.emit(false)
            searchShelf.value = ""
            searchProduct.value = ""
            quantityEdt.value = ""
            _productDetail.emit(null)
        } else {
            _showProductDetail.emit(false)
            searchProduct.value = ""
            quantityEdt.value = ""
            _productDetail.emit(null)
        }
    }
}