package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.changeQuantity

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.databinding.FragmentChangeQuantityBinding
import eryaz.software.activegroup.ui.base.BaseBottomSheetDialogFragmentKt
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.toIntOrZero
import kotlinx.coroutines.flow.observeOn
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ChangeQuantityFragment : BaseBottomSheetDialogFragmentKt() {
    private val safeArgs by navArgs<ChangeQuantityFragmentArgs>()

    override val viewModel by viewModel<ChangeQuantityVM> {
        parametersOf(safeArgs.orderDetailId, safeArgs.quantityWillBePicked)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChangeQuantityBinding.inflate(layoutInflater)
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

        viewModel.navigateToOrder.asLiveData().observe(viewLifecycleOwner) {
            setFragmentResult(
                ChangeQuantityFragmentRequest,
                bundleOf(ChangeQuantityFragmentKey to true)
            )

            findNavController().navigateUp()

        }

        viewModel.amount.observe(this) {
            if (it.isNotEmpty())
                viewModel.setAmount(it)
        }
    }

    companion object {
        const val ChangeQuantityFragmentRequest =
            "ChangeQuantityFragmentKey"
        const val ChangeQuantityFragmentKey =
            "change_quantity_fragment_key"
    }
}