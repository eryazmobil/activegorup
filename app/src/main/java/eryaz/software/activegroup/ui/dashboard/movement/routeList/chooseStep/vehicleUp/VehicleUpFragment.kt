package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.enums.SoundEnum
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.databinding.FragmentVehicleUpBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp.adapter.VehicleUpPackageAdapter
import eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp.camera.CameraBarcodeFragment
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogFragment
import eryaz.software.activegroup.util.adapter.movement.packageList.VehiclePackageAdapter
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.parcelable
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class VehicleUpFragment : BaseFragment() {

    private val safeArgs by navArgs<VehicleUpFragmentArgs>()

    override val viewModel by viewModel<VehicleUpVM> {
        parametersOf(safeArgs.vehicleID, safeArgs.driverId)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentVehicleUpBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun subscribeToObservables() {
        binding.searchEdt.setOnEditorActionListener { _, actionId, _ ->
            val isValidBarcode = viewModel.packageCode.value.trim().isNotEmpty()
            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.readOrderPackage(viewModel.packageCode.value.trim())
            }

            hideSoftKeyboard()
            true
        }

        viewModel.vehicleDownSuccess
            .asLiveData()
            .observe(this) {
                if (it) {
                    binding.searchEdt.requestFocus()
                    Toast.makeText(context, getString(R.string.unload_package), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        viewModel.vehicleFinished
            .asLiveData()
            .observe(this) {
                if (it) {
                    Toast.makeText(
                        context,
                        getString(R.string.finished_process),
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigateUp()
                }
            }

        viewModel.packageList
            .asLiveData()
            .observe(this) {
                adapter.submitList(it)
            }

        setFragmentResultListener(CameraBarcodeFragment.REQUEST_KEY) { _, bundle ->
            bundle.getString(CameraBarcodeFragment.ARG_BARCODE)
                .let {
                    playSound(SoundEnum.Success)

                    Log.d("TAG", "subscribeToObservables: $it")
                    viewModel.readOrderPackage(it.orEmpty())
                }
        }
    }

    private val adapter by lazy {
        VehicleUpPackageAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.vehicleDownBtn.setOnSingleClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Digər depoya gedəcək məhsulları endirdiyinizə əminsiniz?")

            builder.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.updateReturnShipmentByOrderHeaderIdForUp()
            }

            builder.setNegativeButton(R.string.no) { _, _ ->

            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }
//
//        adapter.onItemClick = {
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("Məhsulun depoya geri qaytarılacağına əminsinizmi?")
//
//            builder.setPositiveButton(R.string.yes) { _, _ ->
//                viewModel.updateReturnShipmentByOrderHeaderIdForUp(it.orderHeaderId)
//            }
//
//            builder.setNegativeButton(R.string.no) { _, _ ->
//
//            }
//
//            val alertDialog: AlertDialog = builder.create()
//            alertDialog.show()
//
//        }

        binding.searchPackageBarcode.setEndIconOnClickListener {
            findNavController().navigate(
                VehicleUpFragmentDirections.actionVehicleUpFragmentToCameraBarcodeFragment()
            )
        }
    }

    override fun onStart() {
        super.onStart()

        binding.searchEdt.requestFocus()
    }
}