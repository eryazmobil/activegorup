package eryaz.software.activegroup.ui.dashboard.settings.companies

import eryaz.software.activegroup.data.models.dto.CompanyDto
import eryaz.software.activegroup.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompanyListVM(
     val companyListDto :List<CompanyDto>
) : BaseViewModel() {

    private val _companyList = MutableStateFlow(companyListDto)
    val companyList = _companyList.asStateFlow()
}