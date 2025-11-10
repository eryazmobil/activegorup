package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.R
import eryaz.software.activegroup.databinding.FragmentVehicleUpBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.util.adapter.movement.packageList.VehiclePackageAdapter
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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
                viewModel.readOrderPackage()
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
    }

    private val adapter by lazy {
        VehiclePackageAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.vehicleDownBtn.setOnSingleClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Endirilmənin bitməsinə əminsiniz mi?")

            builder.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.updateOrderHeaderRouteFinish()
            }

            builder.setNegativeButton(R.string.no) { _, _ ->

            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }

        adapter.onItemClick = {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Məhsulun depoya geri qaytarılacağına əminsinizmi?")

            builder.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.updateReturnShipmentByOrderHeaderIdForUp(it.orderHeaderId)
            }

            builder.setNegativeButton(R.string.no) { _, _ ->

            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()

        }
    }

    override fun onStart() {
        super.onStart()

        binding.searchEdt.requestFocus()
    }
}