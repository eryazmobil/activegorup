package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.enums.SoundEnum
import eryaz.software.activegroup.data.models.dto.ButtonDto
import eryaz.software.activegroup.data.models.dto.ConfirmationDialogDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.databinding.FragmentFastCountingDetailBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.counting.firstCounting.firstCountingDetail.FirstCountingDetailFragmentDirections
import eryaz.software.activegroup.ui.dashboard.inbound.acceptance.acceptanceProcess.AcceptanceProcessFragmentDirections
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogFragment
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.onBackPressedCallback
import eryaz.software.activegroup.util.extensions.parcelable
import eryaz.software.activegroup.util.extensions.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FastCountingDetailFragment : BaseFragment() {
    private val safeArgs by navArgs<FastCountingDetailFragmentArgs>()

    override val viewModel by viewModel<FastCountingDetailVM> {
        parametersOf(safeArgs.headerId)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentFastCountingDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun setClicks() {
        binding.shelfAddressEdt.requestFocus()

        binding.toolbar.setNavigationOnClickListener {
            showConditionDialog(
                ConfirmationDialogDto(
                    title = getString(R.string.exit),
                    message = getString(R.string.are_you_sure),
                    positiveButton = ButtonDto(
                        text = R.string.yes,
                        onClickListener = {
                            findNavController().navigateUp()
                        }),
                    negativeButton = ButtonDto(text = R.string.no,
                        onClickListener = { confirmationDialog.dismiss() })
                )
            )
        }

        binding.searchProductTil.setEndIconOnClickListener {
            findNavController().navigate(
                FastCountingDetailFragmentDirections.actionFastCountingDetailFragmentToProductListDialogFragment()
            )
        }

        binding.toolbar.setMenuOnClickListener {
            findNavController().navigate(
                FastCountingDetailFragmentDirections.actionFastCountingDetailFragmentToAssignedShelfFragment(
                    viewModel.stHeaderId
                )
            )
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

        binding.addProductBtn.setOnSingleClickListener {
            binding.saveBtn.visibility = View.VISIBLE
            viewModel.addProductToList()
            binding.searchProductEdt.requestFocus()
        }

        binding.infoBtn.setOnSingleClickListener {
            findNavController().navigate(
                FastCountingDetailFragmentDirections.actionFastCountingDetailFragmentToFastWillCountedListFragment(
                    viewModel.willCountedProductList.toTypedArray()
                )
            )
        }

        binding.saveBtn.setOnSingleClickListener {
            Log.d("TAG", "setClicks: ${viewModel.isValidFinish()}")
            if (!viewModel.isValidFinish())
                return@setOnSingleClickListener

            errorDialog.show(context,
                ErrorDialogDto(
                    titleRes = R.string.different_value_enter,
                    messageRes = R.string.different_value_enter_sure,
                    positiveButton = ButtonDto(
                        text = R.string.yes,
                        onClickListener = {
                            errorDialog.show(
                                context, ErrorDialogDto(
                                    titleRes = R.string.sure,
                                    messageRes = R.string.sure_no_undo,
                                    positiveButton = ButtonDto(
                                        text = R.string.yes,
                                        onClickListener = {
                                            viewModel.saveBtn()
                                        }
                                    ),
                                    negativeButton = ButtonDto(
                                        text = R.string.no
                                    )
                                )
                            )
                        }
                    ),
                    negativeButton = ButtonDto(
                        text = R.string.no
                    )
                )
            )
        }
    }

    override fun subscribeToObservables() {

        setFragmentResultListener(ProductListDialogFragment.REQUEST_KEY) { _, bundle ->
            val dto = bundle.parcelable<ProductDto>(ProductListDialogFragment.ARG_PRODUCT_DTO)
            dto?.let {
                viewModel.setEnteredProduct(it)
            }
        }

        viewModel.barcodeSuccess
            .asLiveData()
            .observe(viewLifecycleOwner) {
                playSound(SoundEnum.Success)
            }

        viewModel.readShelfBarcode.observe(this) {
            if (it) {
                binding.searchProductEdt.requestFocus()
            }
        }

        viewModel.showProductDetail.observe(this) {
            if (it) {
                binding.quantityEdt.requestFocus()
            }
        }

        viewModel.hasNotProductBarcode.observe(this) {
            if (it) {
                errorDialog.show(
                    context, ErrorDialogDto(
                        titleRes = R.string.error,
                        messageRes = R.string.msg_no_barcode_and_new_barcode,
                        positiveButton = ButtonDto(text = R.string.yes, onClickListener = {
                            findNavController().navigate(
                                FirstCountingDetailFragmentDirections.actionFirstCountingDetailFragmentToCreateBarcodeDialog()
                            )
                            errorDialog.dismiss()
                        }),
                        negativeButton = ButtonDto(text = R.string.no, onClickListener = {
                            errorDialog.dismiss()
                            toast(getString(R.string.process_cancelled))
                        })
                    )
                )
            }
        }

        viewModel.showProductDetail.observe(this) {
            if (it) {
                binding.quantityEdt.requestFocus()
            }
        }

        viewModel.productDetail
            .asLiveData()
            .observe(viewLifecycleOwner) {
                if (it != null) {
                    binding.quantityEdt.requestFocus()
                }
            }

        viewModel.actionAddProduct
            .asLiveData()
            .observe(viewLifecycleOwner) {
                if (it) {
                    binding.searchProductEdt.requestFocus()
                    toast(getString(R.string.msg_process_success))
                }
            }

        viewModel.actionIsFinished.observe(this) {
            if (it) {
                binding.shelfAddressEdt.requestFocus()
            }
        }
    }

}



