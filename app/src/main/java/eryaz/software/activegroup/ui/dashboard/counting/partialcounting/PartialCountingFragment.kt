package eryaz.software.activegroup.ui.dashboard.counting.partialcounting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.ButtonDto
import eryaz.software.activegroup.data.models.dto.ConfirmationDialogDto
import eryaz.software.activegroup.databinding.FragmentPartialCountingBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.onBackPressedCallback
import org.koin.androidx.viewmodel.ext.android.viewModel

class PartialCountingFragment : BaseFragment() {

    override val viewModel by viewModel<PartialCountingVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentPartialCountingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root

    }

    override fun setClicks() {

        binding.shelfAddressEdt.requestFocus()

        onBackPressedCallback {
            showConditionDialog(
                ConfirmationDialogDto(
                    title = getString(R.string.exit),
                    message = getString(R.string.are_you_sure),
                    positiveButton = ButtonDto(text = R.string.yes, onClickListener = {
                        backToPage()
                    }),
                    negativeButton = ButtonDto(text = R.string.no,
                        onClickListener = { confirmationDialog.dismiss() })
                )
            )
        }
        binding.toolbar.setNavigationOnClickListener {
            backToPage()
        }

        binding.shelfAddressEdt.setOnEditorActionListener { _, actionId, _ ->

            val isValidBarcode = viewModel.searchShelf.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.getShelfByCode()
            }

            hideSoftKeyboard()
            true
        }

        binding.searchProductEdt.setOnEditorActionListener { _, actionId, _ ->

            val isValidBarcode = viewModel.searchProduct.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.getBarcodeByCode()
            }

            hideSoftKeyboard()
            true
        }

        binding.completeCounting.setOnSingleClickListener {
            viewModel.finishPartialStockTacking {
                backToPage()
            }
        }

        viewModel.shelfIsValid.observe(this) {
            binding.newShelfButton.isEnabled = it
            binding.shelfAddressEdt.isEnabled=!it
            if (it) {
                binding.searchProductEdt.requestFocus()
            }
        }

        viewModel.showProductDetail.observe(this) {
            if (it) {
                binding.quantityEdt.requestFocus()
            }
        }

        binding.newShelfButton.setOnSingleClickListener {
            viewModel.nextPartialStockTackingDetail {
                binding.shelfAddressEdt.requestFocus()
            }
        }

        binding.continueToCounting.setOnSingleClickListener {
            viewModel.createSTActionProcessFromPartialStockTaking()
            binding.searchProductEdt.requestFocus()
        }
    }

    private fun backToPage() {
        findNavController().navigateUp()
    }
}

