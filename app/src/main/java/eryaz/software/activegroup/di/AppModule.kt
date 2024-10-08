package eryaz.software.activegroup.di

import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleDown.orderDetailViewPager.packageDetail.PackageDetailVM
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp.VehicleUpVM
import eryaz.software.activegroup.data.enums.DashboardPermissionType
import eryaz.software.activegroup.data.models.dto.CompanyDto
import eryaz.software.activegroup.data.models.dto.CountingComparisonDto
import eryaz.software.activegroup.data.models.dto.OrderHeaderDto
import eryaz.software.activegroup.data.models.dto.PackageDto
import eryaz.software.activegroup.data.models.dto.WarehouseDto
import eryaz.software.activegroup.ui.auth.LoginViewModel
import eryaz.software.activegroup.ui.dashboard.DashboardViewModel
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.FastCountingListVM
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.dialog.EditQuantityVM
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.FastCountingDetailVM
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf.AssignedShelfVM
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.willCounted.FastWillCountedListVM
import eryaz.software.activegroup.ui.dashboard.counting.firstCounting.FirstCountingListVM
import eryaz.software.activegroup.ui.dashboard.counting.firstCounting.firstCountingDetail.FirstCountingDetailVM
import eryaz.software.activegroup.ui.dashboard.counting.firstCounting.firstCountingDetail.countingInfo.InfoFirstCountingVM
import eryaz.software.activegroup.ui.dashboard.counting.partialcounting.PartialCountingVM
import eryaz.software.activegroup.ui.dashboard.dashboardDetail.DashboardDetailViewModel
import eryaz.software.activegroup.ui.dashboard.inbound.acceptance.AcceptanceListVM
import eryaz.software.activegroup.ui.dashboard.inbound.acceptance.acceptanceProcess.AcceptanceProcessVM
import eryaz.software.activegroup.ui.dashboard.inbound.acceptance.acceptanceProcess.createBarcode.CreateBarcodeDialogVM
import eryaz.software.activegroup.ui.dashboard.inbound.acceptance.acceptanceProcess.waybillDetail.WaybillDialogVM
import eryaz.software.activegroup.ui.dashboard.inbound.crossdockList.CrossDockListVM
import eryaz.software.activegroup.ui.dashboard.inbound.dat.acceptance.DatAcceptanceListVM
import eryaz.software.activegroup.ui.dashboard.inbound.dat.acceptance.acceptanceProcess.DatAcceptanceProcessVM
import eryaz.software.activegroup.ui.dashboard.inbound.dat.placement.DatPlacementListVM
import eryaz.software.activegroup.ui.dashboard.inbound.dat.placement.placementDetail.DatPlacementDetailVM
import eryaz.software.activegroup.ui.dashboard.inbound.placement.PlacementListVM
import eryaz.software.activegroup.ui.dashboard.inbound.placement.placementDetail.PlacementDetailVM
import eryaz.software.activegroup.ui.dashboard.movement.driverList.DriverListVM
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.ChooseVehicleVM
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleDown.VehicleDownVM
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleDown.orderDetailViewPager.OrderDetailViewPagerVM
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleDown.orderDetailViewPager.orderDetail.OrderDetailVM
import eryaz.software.activegroup.ui.dashboard.movement.transferAllShelf.TransferAllShelfVM
import eryaz.software.activegroup.ui.dashboard.movement.transferShelf.TransferShelfVM
import eryaz.software.activegroup.ui.dashboard.movement.transferStockCorrection.TransferStockCorrectionVM
import eryaz.software.activegroup.ui.dashboard.movement.transferStockCorrection.stockType.StockTyeDialogVM
import eryaz.software.activegroup.ui.dashboard.movement.transferStockCorrection.storageList.StorageListDialogVM
import eryaz.software.activegroup.ui.dashboard.movement.transferStorage.TransferStorageVM
import eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.ControlPointListVM
import eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.OrderHeaderListVM
import eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.controlPointDetail.ControlPointDetailVM
import eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.controlPointDetail.addPackage.AddPackageControlVM
import eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.controlPointDetail.updatePackage.UpdatePackageControlVM
import eryaz.software.activegroup.ui.dashboard.outbound.datControl.ControlPointTransferListVM
import eryaz.software.activegroup.ui.dashboard.outbound.datControl.datControlDetail.TransferControlPointDetailVM
import eryaz.software.activegroup.ui.dashboard.outbound.datPicking.DatPickingListVM
import eryaz.software.activegroup.ui.dashboard.outbound.datPicking.datPickingDetail.DatPickingDetailVM
import eryaz.software.activegroup.ui.dashboard.outbound.datPicking.datPickingDetail.dialog.TransferDetailListDialogVM
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.OrderPickingListVM
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.OrderPickingDetailVM
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.changeQuantity.ChangeQuantityVM
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.dialog.OrderDetailListDialogVM
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.dialog.ShelfListDialogVM
import eryaz.software.activegroup.ui.dashboard.query.queryShelf.QueryShelfFragmentVM
import eryaz.software.activegroup.ui.dashboard.query.queryStorage.QueryStorageFragmentVM
import eryaz.software.activegroup.ui.dashboard.recording.createVerifyShelf.VarietyShelfCreateVM
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogVM
import eryaz.software.activegroup.ui.dashboard.recording.recordBarcode.RecordBarcodeVM
import eryaz.software.activegroup.ui.dashboard.settings.SettingsViewModel
import eryaz.software.activegroup.ui.dashboard.settings.appLock.AppLockVM
import eryaz.software.activegroup.ui.dashboard.settings.changeLanguage.LanguageVM
import eryaz.software.activegroup.ui.dashboard.settings.changePassword.ChangePasswordVM
import eryaz.software.activegroup.ui.dashboard.settings.companies.CompanyListVM
import eryaz.software.activegroup.ui.dashboard.settings.warehouses.WarehouseListVM
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    //Login
    viewModel { LoginViewModel(get(), get()) }

    //Dashboard
    viewModel { DashboardViewModel(repo = get()) }

    //DashboardDetail
    viewModel { (permissionType: DashboardPermissionType) ->
        DashboardDetailViewModel(
            repo = get(), permissionType = permissionType
        )
    }

    //SettingViewModel
    viewModel { SettingsViewModel(repo = get(), authRepo = get()) }

    //ChangePasswordVM
    viewModel { ChangePasswordVM(repo = get()) }

    // AcceptanceListVM
    viewModel { AcceptanceListVM(repo = get()) }

    // AcceptanceProcessVM
    viewModel { AcceptanceProcessVM(repo = get()) }

    // DatAcceptanceListVM
    viewModel { DatAcceptanceListVM(repo = get()) }

    // DatAcceptanceProcessVM
    viewModel { DatAcceptanceProcessVM(repo = get()) }

    // WaybillDialogVM
    viewModel { WaybillDialogVM(repo = get()) }

    // WaybillDialogVM
    viewModel { CreateBarcodeDialogVM(repo = get()) }

    //PlacementListVM
    viewModel { PlacementListVM(repo = get()) }

    //DatPlacementListVM
    viewModel { DatPlacementListVM(repo = get()) }

    //PlacementDetailVM
    viewModel { PlacementDetailVM(repo = get()) }

    //DatPlacementDetailVM
    viewModel { DatPlacementDetailVM(repo = get()) }

    //CrossDock Acceptance module
    viewModel { CrossDockListVM(repo = get()) }

    //TransferStorage
    viewModel { TransferStorageVM(repo = get()) }

    //TransferSelf
    viewModel { TransferShelfVM(repo = get()) }

    //TransferAllSelf
    viewModel { TransferAllShelfVM(repo = get()) }

    //StorageList
    viewModel { StorageListDialogVM(userRepo = get()) }

    //TransferStockCorrection
    viewModel { TransferStockCorrectionVM(repo = get()) }

    //StockTyeDialogVM
    viewModel { StockTyeDialogVM() }

    //ProductListDialog
    viewModel { ProductListDialogVM(repo = get()) }

    //RecordBarcode
    viewModel { RecordBarcodeVM(barcodeRepo = get()) }

    //CompanyList
    viewModel { (companyListDto: List<CompanyDto>) ->
        CompanyListVM(
            companyListDto = companyListDto
        )
    }

    //WarehouseList
    viewModel { (warehouseListDto: List<WarehouseDto>) ->
        WarehouseListVM(warehouseListDto = warehouseListDto)
    }

    //VarietyShelfCreate
    viewModel { VarietyShelfCreateVM(repo = get()) }

    //ProductQueryFragment
    viewModel { QueryStorageFragmentVM(repo = get()) }

    //QueryShelfFragment
    viewModel { QueryShelfFragmentVM(repo = get()) }

    //FirstCounting
    viewModel { FirstCountingListVM(repo = get()) }

    viewModel { (headerId: Int) ->
        AssignedShelfVM(
            repo = get(), stHeaderId = headerId
        )
    }

    //FirstCountingDetail
    viewModel { (stHeaderId: Int) ->
        FirstCountingDetailVM(
            workActivityRepo = get(), countingRepo = get(), stHeaderId = stHeaderId
        )
    }

    viewModel {
        PartialCountingVM(
            countingRepo = get(), workActivityRepo = get()
        )
    }

    //Info Counting
    viewModel { (stHeaderId: Int, selectedShelfId: Int) ->
        InfoFirstCountingVM(
            repo = get(), stHeaderId = stHeaderId, selectedShelfId = selectedShelfId
        )
    }

    //FastCounting
    viewModel { FastCountingListVM(repo = get()) }

    //FastCounting Detail
    viewModel { (stHeaderId: Int) ->
        FastCountingDetailVM(
            workActivityRepo = get(), countingRepo = get(), stHeaderId = stHeaderId
        )
    }

    //FastWillCountedList
    viewModel { (productList: List<CountingComparisonDto>) ->
        FastWillCountedListVM(productList)
    }

    //EditQuantity
    viewModel { (productCode: String, productQuantity: Int, productId: Int) ->
        EditQuantityVM(
            productCode = productCode, productQuantity = productQuantity, productId = productId
        )
    }

    //OrderPicking
    viewModel { OrderPickingListVM(repo = get()) }

    viewModel { OrderPickingDetailVM(orderRepo = get(), workActivityRepo = get()) }

    viewModel { OrderDetailListDialogVM(get()) }

    viewModel { (productId: Int) ->
        ShelfListDialogVM(repo = get(), productId = productId)
    }

    //Control Point
    viewModel {
        ControlPointListVM(repo = get())
    }

    viewModel { (orderHeaderList: List<OrderHeaderDto>) ->
        OrderHeaderListVM(orderHeaderList = orderHeaderList)
    }

    viewModel { (workActivityCode: String, orderHeaderId: Int) ->
        ControlPointDetailVM(
            orderRepo = get(),
            workActivityRepo = get(),
            workActivityCode = workActivityCode,
            orderHeaderId = orderHeaderId
        )
    }

    viewModel { (packageList: List<PackageDto>, orderHeaderId: Int) ->
        AddPackageControlVM(
            get(), packageList = packageList, orderHeaderId = orderHeaderId
        )
    }

    viewModel { (packageDto: PackageDto, packageId: Int) ->
        UpdatePackageControlVM(
            get(), packageDto = packageDto, packageId = packageId
        )
    }

    //DAT
    viewModel {
        DatPickingListVM(get())
    }

    viewModel {
        DatPickingDetailVM(
            get(), get()
        )
    }

    viewModel {
        TransferDetailListDialogVM(get())
    }

    viewModel {
        ControlPointTransferListVM(get())
    }

    viewModel { (workActivityId: Int, workActivityCode: String) ->
        TransferControlPointDetailVM(
            workActivityRepo = get(),
            id = workActivityId,
            workActivityCode = workActivityCode
        )
    }

    viewModel {
        DriverListVM(get())
    }

    viewModel { (id: Int) ->
        ChooseVehicleVM(id)
    }

    viewModel { (vehicleID: Int, driverId: Int) ->
        VehicleDownVM(get(), vehicleID = vehicleID, driverId = driverId)
    }

    viewModel { (vehicleID: Int, driverId: Int) ->
        VehicleUpVM(get(), vehicleID = vehicleID, driverId = driverId)
    }

    viewModel { (shippingRouteId: Int, orderHeaderId: Int) ->
        PackageDetailVM(shippingRouteId = shippingRouteId, orderHeaderId = orderHeaderId, get())
    }
    viewModel { (orderHeaderId: Int) ->
        OrderDetailVM(orderHeaderId = orderHeaderId, get())
    }

    viewModel { (shippingRouteId: Int, orderHeaderId: Int) ->
        OrderDetailViewPagerVM(shippingRouteId = shippingRouteId, orderHeaderId = orderHeaderId)
    }

    viewModel { LanguageVM() }

    viewModel { (orderDetailId: Int, orderQuantity: Int) ->
        ChangeQuantityVM(
            orderRepo = get(),
            orderDetailId = orderDetailId,
            orderQuantity = orderQuantity
        )
    }

    viewModel { AppLockVM() }
}