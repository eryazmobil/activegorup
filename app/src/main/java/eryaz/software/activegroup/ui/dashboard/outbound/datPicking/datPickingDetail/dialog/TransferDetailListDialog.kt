package eryaz.software.activegroup.ui.dashboard.outbound.datPicking.datPickingDetail.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eryaz.software.activegroup.databinding.TransferDetailDialogListBinding
import eryaz.software.activegroup.ui.base.BaseDialogFragment
import eryaz.software.activegroup.util.adapter.outbound.dat.TransferDetailListDialogAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransferDetailListDialog : BaseDialogFragment() {

    override val viewModel by viewModel<TransferDetailListDialogVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        TransferDetailDialogListBinding.inflate(layoutInflater)
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

        val displayMetrics = resources.displayMetrics
        val widthPercentage = 0.8
        val heightPercentage = 0.9

        val width = (displayMetrics.widthPixels * widthPercentage).toInt()
        val height = (displayMetrics.heightPixels * heightPercentage).toInt()

        dialog?.window?.setLayout(width, height)

        viewModel.transferDetailList
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

        viewModel.searchList()
            .observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
    }

    private val adapter by lazy {
        TransferDetailListDialogAdapter().also {
            binding.recyclerView.adapter = it
        }
    }
}