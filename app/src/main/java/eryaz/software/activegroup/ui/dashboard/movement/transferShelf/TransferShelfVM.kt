package eryaz.software.activegroup.ui.dashboard.movement.transferShelf

import android.util.Log
import androidx.lifecycle.viewModelScope
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.data.models.dto.StorageDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransferShelfVM(
    private val repo: WorkActivityRepo
) : BaseViewModel() {

    private var productID: Int = 0
    private var exitStorageId: Int = 0
    private var enterStorageId: Int = 0
    private var storageID: Int = 0
    private var enterShelfEId: Int = 0

    val exitShelfId = MutableStateFlow(0)
    val transferSuccess = MutableSharedFlow<Boolean>()
    val searchProduct = MutableStateFlow("")
    val exitShelfValue = MutableStateFlow("")
    val enterShelfValue = MutableStateFlow("")
    val enteredQuantity = MutableStateFlow("")

    private val _storageName = MutableStateFlow("")
    val storageName = _storageName.asStateFlow()

    private val _productDetail = MutableStateFlow<ProductDto?>(null)
    val productDetail = _productDetail.asStateFlow()

    private val _showProductDetail = MutableStateFlow(false)
    val showProductDetail = _showProductDetail.asStateFlow()

    fun getBarcodeByCode() {

        if (isValidStorage()) {
            executeInBackground(
                showProgressDialog = true,
                showErrorDialog = false
            ) {
                repo.getBarcodeByCode(
                    searchProduct.value.trim(),
                    SessionManager.companyId
                ).onSuccess {
                    productID = it.product.id
                    _productDetail.emit(it.product)
                    _showProductDetail.emit(true)
                }.onError { message, _ ->
                    showError(
                        ErrorDialogDto(
                            title = stringProvider.invoke(R.string.error),
                            message = message
                        )
                    )
                }
            }
        }
    }

    fun getShelfByCode(shelfCode: String, enterShelf: Boolean) {
        executeInBackground(showProgressDialog = true) {
            repo.getShelfByCode(
                code = shelfCode.trim(),
                warehouseId = SessionManager.warehouseId,
                storageId = storageID
            ).onSuccess {
                if (enterShelf) {
                    enterShelfEId = it.shelfId
                } else {
                    exitShelfId.value = it.shelfId
                }
            }
        }
    }

    private fun isValidStorage(): Boolean {
        when {
            storageID == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error,
                        messageRes = R.string.select_storage
                    )
                )
                return false
            }
        }
        return true
    }

    private fun isValidFields(): Boolean {
        when {

            exitShelfId.value == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error,
                        messageRes = R.string.exit_shelf_address
                    )
                )
                return false
            }

            enterShelfEId == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error,
                        messageRes = R.string.enter_shelf_address_transfer
                    )
                )
                return false
            }

            enteredQuantity.value.toInt() == 0 -> {
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error,
                        messageRes = R.string.enter_valid_qty
                    )
                )
                return false
            }

            else ->
                return true
        }
    }

    fun createAddressMovement() {
        if (isValidFields()) {
            executeInBackground {
                repo.createShelfMovement(
                    productId = productID,
                    enterShelfId = enterShelfEId,
                    exitShelfId = exitShelfId.value,
                    quantity = enteredQuantity.value.toInt(),
                    changeVariety = false
                ).onSuccess {
                    transferSuccess.emit(true)
                    productID = 0
                    exitStorageId = 0
                    enterStorageId = 0
                    enterShelfEId = 0

                    exitShelfId.emit(0)
                    searchProduct.emit("")
                    exitShelfValue.emit("")
                    enterShelfValue.emit("")
                    enteredQuantity.emit("")
                    _productDetail.emit(null)
                    _showProductDetail.emit(false)
                }
            }
        }
    }

    fun setEnteredProduct(dto: ProductDto) {
        if (isValidStorage()) {
            productID = dto.id
            viewModelScope.launch {
                Log.d("TAG", "setEnteredProduct: ")
                _productDetail.emit(dto)
                _showProductDetail.emit(true)
            }
        }
    }

    fun setStorage(storageDtoEnter: StorageDto) {
        this.storageID = storageDtoEnter.id
        viewModelScope.launch {
            _storageName.emit(storageDtoEnter.definition)
        }
    }
}