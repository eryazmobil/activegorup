package eryaz.software.activegroup.ui.dashboard.movement.routeList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.databinding.FragmentRouteListBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.movement.driverList.DriverListVM
import eryaz.software.activegroup.util.adapter.movement.route.RouteAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class DriverListFragment : BaseFragment() {
    override val viewModel by viewModel<DriverListVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentRouteListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        viewModel.fetchDriverList()
    }

    override fun subscribeToObservables() {

       viewModel.driverList
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        viewModel.searchList()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
    }

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter.onItemClick = {
            findNavController().navigate(
                DriverListFragmentDirections.actionRouteListFragmentToChooseVehicleFragment(
                    it.id
                )
            )
        }
    }

    private val adapter by lazy {
        RouteAdapter().also {
            binding.recyclerView.adapter = it
        }
    }
}