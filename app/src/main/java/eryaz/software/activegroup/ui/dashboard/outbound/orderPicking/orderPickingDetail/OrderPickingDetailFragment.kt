package eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.enums.SoundEnum
import eryaz.software.activegroup.data.models.dto.ButtonDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.databinding.FragmentOrderPickingDetailBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.changeQuantity.ChangeQuantityFragment
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogFragment
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.onBackPressedCallback
import eryaz.software.activegroup.util.extensions.orZero
import eryaz.software.activegroup.util.extensions.parcelable
import eryaz.software.activegroup.util.extensions.toast
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderPickingDetailFragment : BaseFragment() {
    override val viewModel by viewModel<OrderPickingDetailVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentOrderPickingDetailBinding.inflate(layoutInflater)
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

        binding.toolbar.setNavigationOnClickListener {
            viewModel.checkCrossDockNeedByActionId()
        }

        binding.changeProductQuantity.setOnSingleClickListener {
            findNavController().navigate(
                OrderPickingDetailFragmentDirections.actionOrderPickingDetailFragmentToChangeQuantityFragment(
                    viewModel.selectedOrderDetailProduct?.id.orZero(),
                    viewModel.selectedSuggestion.value?.quantityWillBePicked.orZero()
                )
            )
        }

        binding.toolbar.setMenuOnClickListener {
            popupMenu(it)
        }

        binding.searchProductBarcodeTill.setEndIconOnClickListener {
            findNavController().navigate(
                OrderPickingDetailFragmentDirections.actionOrderPickingDetailFragmentToProductListDialogFragment()
            )
        }

        binding.shelfListBtn.setOnSingleClickListener {
            findNavController().navigate(
                OrderPickingDetailFragmentDirections.actionOrderPickingDetailFragmentToShelfListDialog(
                    viewModel.productId
                )
            )
        }

        binding.pickProductBtn.setOnSingleClickListener {
            viewModel.updateOrderDetailCollectedAddQuantityForPda()
        }
    }

    override fun subscribeToObservables() {

        viewModel.pickProductSuccess.asLiveData().observe(viewLifecycleOwner) {
                playSound(SoundEnum.Success)
            }

        viewModel.parentView.observe(this) {
            if (it) {
                binding.parentView.visibility = View.GONE

                errorDialog.show(
                    context, ErrorDialogDto(
                        titleRes = R.string.warning,
                        messageRes = R.string.work_activity_error_1,
                        positiveButton = ButtonDto(text = R.string.close_screen, onClickListener = {
                            viewModel.checkCrossDockNeedByActionId()
                        })
                    )
                )
            }
        }

        setFragmentResultListener(ProductListDialogFragment.REQUEST_KEY) { _, bundle ->
            val dto = bundle.parcelable<ProductDto>(ProductListDialogFragment.ARG_PRODUCT_DTO)
            dto?.let {
                viewModel.setEnteredProduct(it)
            }
        }

        setFragmentResultListener(ChangeQuantityFragment.ChangeQuantityFragmentRequest) { _, bundle ->
            bundle.getBoolean(ChangeQuantityFragment.ChangeQuantityFragmentKey).let {
                if (it) {
                    viewModel.getOrderDetailPickingList(true)
                }
            }
        }

        viewModel.notAvailableStock.asLiveData().observe(this) {
            if (it) {
                binding.parentView.visibility = View.GONE

                errorDialog.show(
                    context, ErrorDialogDto(
                        titleRes = R.string.warning,
                        messageRes = R.string.work_activity_error_1,
                        positiveButton = ButtonDto(text = R.string.close_screen, onClickListener = {
                            viewModel.checkCrossDockNeedByActionId()
                        })
                    )
                )
            }
        }

        viewModel.pickProductFinish.asLiveData().observe(this) {
            if (it) {
                binding.parentView.visibility = View.GONE

                errorDialog.show(context, ErrorDialogDto(titleRes = R.string.warning,
                    messageRes = R.string.order_was_picking,
                    positiveButton = ButtonDto(text = R.string.close, onClickListener = {
                        errorDialog.dismiss()
                        findNavController().navigateUp()
                    }),
                    negativeButton = ButtonDto(text = R.string.exit, onClickListener = {
                        errorDialog.dismiss()
                        findNavController().navigateUp()
                    })))
            }
        }

        viewModel.createStockOut.observe(this) {
            if (it) {
                toast(getString(R.string.success))
            }
        }

        viewModel.showProductDetail.observe(this) {
            if (it) {
                binding.shelfAddressEdt.requestFocus()
                binding.stateView.setViewVisible(binding.productDetailParent, true)
            }
        }

        binding.searchProductEdt.setOnEditorActionListener { _, actionId, _ ->

            val isValidBarcode = viewModel.productBarcode.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.getBarcodeByCode()
            }

            hideSoftKeyboard()
            true
        }

        binding.shelfAddressEdt.setOnEditorActionListener { _, actionId, _ ->

            val shelfAddress = viewModel.shelfAddress.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && shelfAddress) {
                viewModel.getShelfByCode()
            }

            hideSoftKeyboard()
            true
        }

        viewModel.nextOrder.observe(this) {
            if (it) {
                binding.nextBtn.performClick()
            }
        }

        viewModel.shelfRead.asLiveData().observe(viewLifecycleOwner) {
            if (it) {
                binding.quantityEdt.requestFocus()
            }
        }

        viewModel.finishWorkAction.observe(this) {
            if (it) {
                findNavController().navigateUp()
            }
        }

        viewModel.productRequestFocus.asLiveData().observe(this) {
            if (it) binding.searchProductEdt.requestFocus()
        }
    }

    private fun popupMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.order_picking_menu)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.order_detail_list -> {
                        findNavController().navigate(
                            OrderPickingDetailFragmentDirections.actionOrderPickingDetailFragmentToOrderDetailListDialog()
                        )
                        true
                    }

                    R.id.menu_finish_action -> {
                        activity?.onBackPressed()
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }

    override fun onStart() {
        super.onStart()
        binding.searchProductEdt.requestFocus()
    }
}