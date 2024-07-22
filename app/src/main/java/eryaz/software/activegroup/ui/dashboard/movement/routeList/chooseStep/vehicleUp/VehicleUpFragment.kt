package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.R
import eryaz.software.activegroup.databinding.FragmentVehicleUpBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.util.adapter.movement.packageList.VehiclePackageAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VehicleUpFragment : BaseFragment() {

    private val safeArgs by navArgs<VehicleUpFragmentArgs>()

    override val viewModel by viewModel<VehicleUpVM> {
        parametersOf(safeArgs.vehicleID, safeArgs.routeID)
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
        viewModel.vehicleDownSuccess
            .asLiveData()
            .observe(this) {
                if (it) {
                    binding.searchEdt.requestFocus()
                    viewModel.getOrderHeaderRouteList()
                    Toast.makeText(context, getString(R.string.unload_package), Toast.LENGTH_SHORT)
                        .show()
                }
            }

        viewModel.packageList.asLiveData().observe(this) {
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

        adapter.onItemClick = {
            findNavController().navigate(
                VehicleUpFragmentDirections.actionVehicleUpFragmentToOrderDetailViewPagerFragment(
                    viewModel.routeID, it.orderHeaderId
                )
            )
        }
    }

    override fun onStart() {
        super.onStart()

        binding.searchEdt.requestFocus()
    }
}