package eryaz.software.activegroup.ui.dashboard.counting.fastCounting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.databinding.FragmentFastCountingListBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.counting.adapter.CountingListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FastCountingListFragment : BaseFragment() {
    override val viewModel by viewModel<FastCountingListVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentFastCountingListBinding.inflate(layoutInflater)
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

        viewModel.fetchCountingWorkActivityList()
    }

    override fun subscribeToObservables() {

        viewModel.countingWorkActivityList.asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.assignedUser.asLiveData()
            .observe(viewLifecycleOwner) {
                if (it) {
                    findNavController().navigate(
                        FastCountingListFragmentDirections.actionFastCountingListFragmentToFastCountingDetailFragment(
                            viewModel.stockTackingHeaderId
                        )
                    )
                }
            }
    }

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        adapter.onItemClick = {
            viewModel.stockTackingHeaderId = it.id
            viewModel.getAllAssignedToUser(it.id)
        }
    }

    private val adapter by lazy {
       CountingListAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    companion object {
        const val FAST_COUNTING_WAREHOUSE = 16
    }
}