package eryaz.software.activegroup.ui.dashboard.outbound.controlPoint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.databinding.FragmentControlPointListBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.util.adapter.outbound.ControlPointListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ControlPointListFragment : BaseFragment() {
    override val viewModel by viewModel<ControlPointListVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentControlPointListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewModel.fetchControlPointList()
    }

    override fun subscribeToObservables() {

        viewModel.controlPointList.asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.orderHeaderList.asLiveData().observe(viewLifecycleOwner) { list ->
            if (list.isNotEmpty()) {
                findNavController().navigate(
                    ControlPointListFragmentDirections.actionControlPointListFragmentToOrderHeaderListDialog(
                        list.toTypedArray()
                    )
                )
            }
        }
    }

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setMenuOnClickListener {
            popupMenu(it)
        }

        adapter.onItemClick = {
            viewModel.controlPointId = it.id
            viewModel.getOrderHeaderListByControlPointId()
        }
    }

    private val adapter by lazy {
        ControlPointListAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    private fun popupMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.refresh_menu)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.refresh -> {
                        viewModel.fetchControlPointList()
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }
}