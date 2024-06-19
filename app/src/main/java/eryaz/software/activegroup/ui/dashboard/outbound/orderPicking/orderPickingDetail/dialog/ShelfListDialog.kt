package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.databinding.OrderShelfListDialogBinding
import eryaz.software.activegroup.ui.base.BaseDialogFragment
import eryaz.software.activegroup.ui.dashboard.query.adapter.ShelfProductQuantityAdapter
import eryaz.software.activegroup.ui.dashboard.query.adapter.ShelfQuantityAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ShelfListDialog : BaseDialogFragment() {
    private val safeArgs by navArgs<ShelfListDialogArgs>()

    override val viewModel by viewModel<ShelfListDialogVM> {
        parametersOf(safeArgs.productId)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        OrderShelfListDialogBinding.inflate(layoutInflater)
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

        viewModel.shelfList.asLiveData()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        val displayMetrics = resources.displayMetrics
        val widthPercentage = 0.8
        val heightPercentage = 0.6

        val width = (displayMetrics.widthPixels * widthPercentage).toInt()
        val height = (displayMetrics.heightPixels * heightPercentage).toInt()

        dialog?.window?.setLayout(width, height)
    }

    private val adapter by lazy {
        ShelfQuantityAdapter().also {
            binding.recyclerView.adapter = it
        }
    }
}