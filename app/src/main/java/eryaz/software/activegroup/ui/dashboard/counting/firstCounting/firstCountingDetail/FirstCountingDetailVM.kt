package eryaz.software.activegroup.ui.dashboard.counting.firstCounting.firstCountingDetail

import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.BarcodeDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.StockTackingProcessDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.CountingRepo
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirstCountingDetailVM(
    private val countingRepo: CountingRepo,
    private val workActivityRepo: WorkActivityRepo,
    val stHeaderId: Int
) : BaseViewModel() {

    var assignedShelfId: Int = 0
    private var productID: Int = 0
    private var stDetailId: Int = 0
    private var oldQuantity: Double = 0.0
    private var resultQuantity: Double = 0.0

    private val stockTakingActionProcessList = MutableStateFlow<List<StockTackingProcessDto>>(emptyList())

    val readShelfBarcode = MutableStateFlow(false)
    val searchProduct = MutableStateFlow("")
    val searchShelf = MutableStateFlow("")
    var quantityEdt = MutableStateFlow("")
    val hasNotProductBarcode = MutableStateFlow(false)

    private val _actionAddProduct = MutableStateFlow(false)
    val actionAddProduct = _actionAddProduct.asStateFlow()

    private val _productDetail = MutableStateFlow<BarcodeDto?>(null)
    val productDetail = _productDetail.asStateFlow()

    private val _actionIsFinished = MutableStateFlow(true)
    val actionIsFinished = _actionIsFinished.asStateFlow()

    private val _showProductDetail = MutableStateFlow(false)
    val showProductDetail = _showProductDetail.asStateFlow()

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
                hasNotProductBarcode.emit(true)
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
                assignedShelfId = it.shelfId
                getSTActionProcessListForShelf()
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

    private fun getSTActionProcessListForShelf() {
        executeInBackground(showProgressDialog = true) {
            countingRepo.getSTActionProcessListForShelf(
                stHeaderId = stHeaderId,
                assignedShelfId = assignedShelfId
            ).onSuccess {
                if (it.isNotEmpty()) {
                    stockTakingActionProcessList.emit(it)
                    stDetailId = it[0].stockTakingDetail!!.id
                    getShelfIsOnAssignedUser()
                }
            }
        }
    }

    private fun getShelfIsOnAssignedUser() {
        executeInBackground(showProgressDialog = true) {
            countingRepo.getShelfIsOnAssignedUser(
                stHeaderId = stHeaderId,
                userId = SessionManager.userId,
                shelfId = assignedShelfId
            ).onSuccess {
                if (it) {
                    createSTActionProcessFromSTDetail()
                } else {
                    showError(
                        ErrorDialogDto(
                            titleRes = R.string.error,
                            messageRes = R.string.can_not_counting_in_shelf
                        )
                    )
                }
            }
        }
    }

    private fun createSTActionProcessFromSTDetail() {
        executeInBackground(showProgressDialog = true) {
            countingRepo.createSTActionProcessFromSTDetail(
                stHeaderId = stHeaderId,
                userId = SessionManager.userId,
                assignedShelfId = assignedShelfId
            ).onSuccess {
                readShelfBarcode.emit(true)
            }
        }
    }

    fun addProduct(addOn: Boolean) {
        if (isValidFields()) {
            if (addOn) {
                resultQuantity = oldQuantity + quantityEdt.value.toDouble()
            } else {
                resultQuantity += quantityEdt.value.toDouble()
            }
            //raf miktar ve urunun bos olmama kontrolu
            executeInBackground(
                showProgressDialog = true
            ) {
                countingRepo.addProduct(
                    stHeaderId = stHeaderId,
                    userId = SessionManager.userId,
                    productId = productID,
                    assignedShelfId = assignedShelfId,
                    countedQuantity = resultQuantity.toInt()
                ).onSuccess {
                    _actionAddProduct.emit(true)
                    searchProduct.value = ""
                    quantityEdt.value = ""
                    _showProductDetail.emit(false)
                    _productDetail.emit(null)
                }
            }
        }
    }

    fun checkProductHasControlled(): Boolean {
        val selectedProduct = stockTakingActionProcessList.value.find {
            it.productDto?.id == productID
        }

        oldQuantity = selectedProduct?.shelfCurrentQuantity ?: 0.0
        return selectedProduct != null
    }

    private fun isValidFields(): Boolean {
        when {
            assignedShelfId == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error, messageRes = R.string.enter_shelf_
                    )
                )
                return false
            }

            productID == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error, messageRes = R.string.product_code_empty
                    )
                )
                return false
            }

            quantityEdt.value.isEmpty() || quantityEdt.value.toInt() == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error, messageRes = R.string.enter_valid_qty
                    )
                )
                return false
            }

            else -> return true
        }
    }

    fun saveBtn() {
        executeInBackground(
            showProgressDialog = true
        ) {
            countingRepo.finishSTDetail(
                stDetailId = stDetailId,
                userId = SessionManager.userId
            ).onSuccess {
                _actionIsFinished.emit(true)
                searchShelf.value = ""
                searchProduct.value = ""
                quantityEdt.value = ""
                _showProductDetail.emit(false)
                _productDetail.emit(null)
            }
        }
    }

}