package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.databinding.FragmentAssignedShelfBinding
import eryaz.software.activegroup.ui.base.BaseBottomSheetDialogFragmentKt
import eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf.adapter.ShelfProductQuantityAdapterForCounting
import eryaz.software.activegroup.ui.dashboard.query.adapter.ShelfProductQuantityAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AssignedShelfFragment : BaseBottomSheetDialogFragmentKt() {
    private val safeArgs by navArgs<AssignedShelfFragmentArgs>()

    override val viewModel by viewModel<AssignedShelfVM> {
        parametersOf(safeArgs.headerId)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentAssignedShelfBinding.inflate(layoutInflater)
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
        viewModel.shelfList
            .asLiveData()
            .observe(viewLifecycleOwner) {

                shelfAdapter.submitList(it)
            }
    }

    private val shelfAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ShelfProductQuantityAdapterForCounting().also {
            binding.recyclerViewForShelf.adapter = it
        }
    }
}