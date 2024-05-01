package eryaz.software.activegroup.ui.dashboard.outbound.datPicking.datPickingDetail.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.api.utils.onError
import eryaz.software.activegroup.data.api.utils.onSuccess
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.TransferRequestDetailDto
import eryaz.software.activegroup.data.persistence.TemporaryCashManager
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import eryaz.software.activegroup.ui.base.BaseViewModel
import eryaz.software.activegroup.util.extensions.orZero

class TransferDetailListDialogVM(
    private val repo: WorkActivityRepo
) : BaseViewModel() {

    private val _transferDetailList = MutableLiveData<List<TransferRequestDetailDto>>(emptyList())
    val transferDetailList: LiveData<List<TransferRequestDetailDto>> = _transferDetailList

    val search = MutableLiveData("")

    init {
        getTransferListDetail()
    }

    fun searchList() = search.switchMap { query ->
        MutableLiveData<List<TransferRequestDetailDto>>().apply {
            value = _transferDetailList.value.orEmpty().filter { data ->
                data.product.code.contains(query, ignoreCase = true)
            }
        }
    }

    fun getTransferListDetail() {
        executeInBackground(showProgressDialog = true) {
            val workActivityId =
                TemporaryCashManager.getInstance().workActivity?.workActivityId.orZero()
            repo.getTransferRequestDetailList(workActivityId = workActivityId)
                .onSuccess {
                    _transferDetailList.value = it
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