package eryaz.software.activegroup.ui.dashboard.inbound.placement.placementDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.enums.IconType
import eryaz.software.activegroup.data.enums.SoundEnum
import eryaz.software.activegroup.data.models.dto.ButtonDto
import eryaz.software.activegroup.data.models.dto.ConfirmationDialogDto
import eryaz.software.activegroup.data.models.dto.ErrorDialogDto
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.data.persistence.TemporaryCashManager
import eryaz.software.activegroup.databinding.FragmentPlacementDetailBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogFragment
import eryaz.software.activegroup.util.adapter.inbound.adapter.SuggestionShelfListAdapter
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.dialogs.QuestionDialog
import eryaz.software.activegroup.util.extensions.copyToClipboard
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.onBackPressedCallback
import eryaz.software.activegroup.util.extensions.parcelable
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlacementDetailFragment : BaseFragment() {
    override val viewModel by viewModel<PlacementDetailVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentPlacementDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding.placementViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun setClicks() {

        onBackPressedCallback {
            showConditionDialog(
                ConfirmationDialogDto(
                    title = getString(R.string.exit),
                    message = getString(R.string.are_you_sure),
                    positiveButton = ButtonDto(text = R.string.yes, onClickListener = {
                        backToPage()
                    }),
                    negativeButton = ButtonDto(
                        text = R.string.no,
                        onClickListener = { confirmationDialog.dismiss() })
                )
            )
        }

        binding.toolbar.setNavigationOnClickListener {
            backToPage()
        }

        binding.toolbar.setMenuOnClickListener {
            popupMenu(it)
        }

        binding.searchProductBarcodePlacement.setEndIconOnClickListener {
            findNavController().navigate(
                PlacementDetailFragmentDirections.actionPlacementDetailFragmentToProductListDialogFragment()
            )
        }

        binding.searchEdt.setOnEditorActionListener { _, actionId, _ ->

            val isValidBarcode = viewModel.searchProduct.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.getBarcodeByCode()
            }

            hideSoftKeyboard()
            true
        }

        binding.shelfAddressEdt.setOnEditorActionListener { _, actionId, _ ->

            val isValidBarcode = viewModel.searchShelf.value.trim().isNotEmpty()

            if ((actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_DONE) && isValidBarcode) {
                viewModel.getShelfByCode()
            }

            hideSoftKeyboard()
            true
        }

        binding.controlBtn.setOnSingleClickListener {
            when {
                viewModel.isQuantityValid() -> {
                    errorDialog.show(
                        context = context,
                        ErrorDialogDto(
                            messageRes = R.string.msg_placement_qty_must_more_than_0
                        )
                    )
                }

                else -> QuestionDialog(
                    onPositiveClickListener = {
                        viewModel.updateWaybillControlAddQuantity()
                    },
                    textHeader = resources.getString(R.string.verify_qty),
                    textMessage = viewModel.resultAmountTxt.value,
                    positiveBtnText = resources.getString(R.string.yes),
                    negativeBtnText = resources.getString(R.string.no),
                    negativeBtnViewVisible = true,
                    icType = IconType.Warning.ordinal
                ).show(parentFragmentManager, "dialog")

            }
        }

        adapter.onItemClick = {
            context?.copyToClipboard(it.shelfAddress)
            Toast.makeText(context, "Kopyalandı", Toast.LENGTH_SHORT).show()
        }
    }

    override fun subscribeToObservables() {

        viewModel.controlSuccess
            .asLiveData()
            .observe(viewLifecycleOwner) {
                playSound(SoundEnum.Success)
        }

        setFragmentResultListener(ProductListDialogFragment.REQUEST_KEY) { _, bundle ->
            val dto = bundle.parcelable<ProductDto>(ProductListDialogFragment.ARG_PRODUCT_DTO)
            dto?.let {
                viewModel.setEnteredProduct(it)
            }
        }

        viewModel.controlSuccess.asLiveData().observe(viewLifecycleOwner) {
            binding.searchEdt.setText("")
            binding.quantityEdt.setText("")
            binding.shelfAddressEdt.setText("")
            binding.searchEdt.requestFocus()
        }

        viewModel.showProductDetailView.asLiveData().observe(viewLifecycleOwner) {
            binding.quantityEdt.requestFocus()
        }

        viewModel.suggestionShelfList.asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.checkProductInPlacementList
            .asLiveData()
            .observe(viewLifecycleOwner) {
                if (it) {
                    //  showLottieAnimation(R.raw.placement_anim)
                    backToPage()
                }
            }
    }

    private val adapter by lazy {
        SuggestionShelfListAdapter().also {
            binding.suggestionShelfRecycleView.adapter = it
        }
    }

    private fun popupMenu(view: View) {
        PopupMenu(requireContext(), view).apply {
            inflate(R.menu.placement_menu)

            setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.menu_list_detail -> {
                        findNavController().navigate(
                            PlacementDetailFragmentDirections.actionPlacementDetailFragmentToWaybillDetailListDialog()
                        )
                        true
                    }

                    R.id.menu_finish_action -> {
                        backToPage()
                        true
                    }

                    else -> false
                }
            }
            show()
        }
    }

    private fun backToPage() {
        viewModel.finishWorkAction()
        viewModel.actionIsFinished.asLiveData().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
            TemporaryCashManager.getInstance().workActivity = null
            TemporaryCashManager.getInstance().workAction = null
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchEdt.requestFocus()
    }
}