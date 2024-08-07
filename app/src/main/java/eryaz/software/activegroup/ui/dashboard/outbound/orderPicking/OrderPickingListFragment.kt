package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.persistence.TemporaryCashManager
import eryaz.software.activegroup.databinding.FragmentOrderPickingListBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.adapter.OrderWorkActivityListAdapter
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.OrderPickingDetailFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderPickingListFragment : BaseFragment() {
    override val viewModel by viewModel<OrderPickingListVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentOrderPickingListBinding.inflate(layoutInflater)
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

    override fun subscribeToObservables() {
        viewModel.getActiveWorkAction()

        viewModel.orderPickingList
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        viewModel.searchList()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        viewModel.navigateToDetail
            .asLiveData()
            .observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(
                        OrderPickingListFragmentDirections.actionOrderPickingListFragmentToOrderPickingDetailFragment()
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
            if (it.isLocked) {
                errorDialog.show(
                    context,
                    ErrorDialogDto(
                        titleRes = R.string.warning,
                        messageRes = R.string.locked_work_activity
                    )
                )
            } else {
                TemporaryCashManager.getInstance().workActivity = it
                viewModel.getWorkActionForPda()
            }
        }
    }

    private val adapter by lazy {
        OrderWorkActivityListAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    private fun popupMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.refresh_menu)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.refresh -> {
                        viewModel.getShippingWorkActivityList()
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }

}