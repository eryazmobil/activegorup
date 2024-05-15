package eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.controlPointDetail

import androidx.lifecycle.viewModelScope
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.BarcodeDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.OrderDetailDto
import eryaz.software.activegroup.data.models.dto.PackageDto
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.extensions.orZero
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ControlPointDetailVM(
    private val orderRepo: OrderRepo,
    private val workActivityRepo: WorkActivityRepo,
    val workActivityCode: String,
    val orderHeaderId: Int
) : BaseViewModel() {

    private var packageListPosition: Int = 0
    private var productID: Int = 0
    private var isPackage: Boolean = false
    var selectedPackageId: Int = 0
    var selectedPackageDto: PackageDto? = null

    val serialCheckBox = MutableStateFlow(false)
    var quantityCollected = MutableStateFlow("")
    var quantityShipped = MutableStateFlow("")
    var quantityOrder = MutableStateFlow("")
    val searchProduct = MutableStateFlow("")
    val quantity = MutableStateFlow("")

    private val _orderDetailList = MutableStateFlow(listOf<OrderDetailDto>())
    val orderDetailList = _orderDetailList.asStateFlow()

    private val _packageList = MutableStateFlow(listOf<PackageDto>())
    val packageList = _packageList.asStateFlow()

    private val _showSpinnerList = MutableStateFlow(false)
    val showSpinnerList = _showSpinnerList.asStateFlow()

    private val _productCode = MutableStateFlow("")
    val productCode = _productCode.asStateFlow()

    private val _productDetail = MutableStateFlow<ProductDto?>(null)
    val productDetail = _productDetail.asStateFlow()

    private val _showProductDetail = MutableStateFlow(false)
    val showProductDetail = _showProductDetail.asStateFlow()

    private val _scrollToPosition = MutableSharedFlow<Int>()
    val scrollToPosition = _scrollToPosition.asSharedFlow()

    init {
        getOrderListDetail()
        getPackageList()
    }

    private fun getOrderListDetail() {
        executeInBackground(showProgressDialog = true) {
            orderRepo.getOrderDetailList(headerId = orderHeaderId).onSuccess {
                if (it.isNotEmpty()) {
                    _orderDetailList.emit(it)

                    calculateDatQuantity()
                } else {
                    showError(
                        ErrorDialogDto(
                            titleRes = R.string.error, messageRes = R.string.no_data_to_list
                        )
                    )
                }
            }.onError { message, _ ->
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error, message = message
                    )
                )
            }
        }
    }

    fun getBarcodeByCode() {
        executeInBackground(
            showErrorDialog = false, showProgressDialog = true
        ) {
            workActivityRepo.getBarcodeByCode(
                code = searchProduct.value.trim(), companyId = SessionManager.companyId
            ).onSuccess {
                productID = it.product.id

                orderDetailList.value.indexOfFirst { dto -> dto.product.id == productID }
                    .takeIf { index -> index >= 0 }?.apply {
                        _scrollToPosition.emit(this)
                    }

                if (serialCheckBox.value) {
                    addQuantityForControl(1)
                } else {
                    _showProductDetail.emit(true)
                    _productDetail.emit(it.product)
                }

            }.onError { _, _ ->
                showError(
                    ErrorDialogDto(
                        titleRes = R.string.error, messageRes = R.string.msg_no_barcode
                    )
                )
            }
        }
    }

    fun addQuantityForControl(quantity: Int) {
        executeInBackground(showProgressDialog = true, hasNextRequest = true) {
            orderRepo.addQuantityForControl(
                orderHeaderId = orderHeaderId,
                productId = productID,
                quantity = quantity,
                isControlDoubleClick = false,
                isPackage = isPackage,
                packageId = selectedPackageId
            ).onSuccess {
                if (it.isNotEmpty()) {
                    getOrderListDetail()
                }
            }
        }
    }

    fun getPackageList() {
        executeInBackground {
            orderRepo.getPackageList(orderHeaderId = orderHeaderId).onSuccess {
                if (it.isNotEmpty()) {
                    _showSpinnerList.emit(true)
                    val itemList = mutableListOf(
                        PackageDto(
                            no = stringProvider.invoke(R.string.choose_package)
                        )
                    )
                    itemList.addAll(it)
                    _packageList.emit(itemList)
                }
            }
        }
    }

    fun setSelectedPackagePosition(position: Int) {
        this.packageListPosition = position
        isPackage = true
        selectedPackageId = _packageList.value[position].id.orZero()
        selectedPackageDto = _packageList.value[position]
    }

    fun getSelectedPackagePosition(): Int {
        return this.packageListPosition
    }

    private fun calculateDatQuantity() {
        var sumQuantityCollected = 0
        var sumQuantityShipped = 0
        var sumQuantity = 0

        for (dto in _orderDetailList.value) {
            sumQuantityCollected += dto.quantityCollected.toInt()
            sumQuantityShipped += dto.quantityShipped.toInt()
            sumQuantity += dto.quantity.toInt()
        }

        viewModelScope.launch {
            quantityCollected.emit(sumQuantityCollected.toString())
            quantityShipped.emit(sumQuantityShipped.toString())
            quantityOrder.emit(sumQuantity.toString())
        }
    }

    fun setEnteredProduct(dto: ProductDto) {
        productID = dto.id
        viewModelScope.launch {
            _productDetail.emit(dto)
            _showProductDetail.emit(true)
        }
    }
}