package eryaz.software.activegroup.ui.dashboard.outbound.controlPoint.orderHeaderDialog.controlPointDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.enums.SoundEnum
import eryaz.software.activegroup.data.models.dto.ProductDto
import eryaz.software.activegroup.databinding.FragmentControlPointDetailBinding
import eryaz.software.activegroup.ui.base.BaseFragment
import eryaz.software.activegroup.ui.dashboard.outbound.orderPicking.orderPickingDetail.OrderPickingDetailFragmentDirections
import eryaz.software.activegroup.ui.dashboard.recording.dialog.ProductListDialogFragment
import eryaz.software.activegroup.util.adapter.outbound.ControlPointDetailListAdapter
import eryaz.software.activegroup.util.adapter.outbound.ControlPointDetailListVH
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.hideSoftKeyboard
import eryaz.software.activegroup.util.extensions.observe
import eryaz.software.activegroup.util.extensions.parcelable
import eryaz.software.activegroup.util.extensions.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class ControlPointDetailFragment : BaseFragment() {
    private val safeArgs by navArgs<ControlPointDetailFragmentArgs>()

    override val viewModel by viewModel<ControlPointDetailVM> {
        parametersOf(safeArgs.workActivityCode, safeArgs.orderHeaderId)
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentControlPointDetailBinding.inflate(layoutInflater)
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

    override fun setClicks() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchProductTill.setEndIconOnClickListener {
            findNavController().navigate(
                ControlPointDetailFragmentDirections.actionControlPointDetailFragmentToProductListDialogFragment()
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

        binding.controlBtn.setOnSingleClickListener {
            viewModel.addQuantityForControl(viewModel.quantity.value.toInt())
        }

        binding.packageSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    viewModel.setSelectedPackagePosition(position)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {

                }
            }

        binding.detailPackage.setOnSingleClickListener {
            viewModel.getPackageList()
        }

        binding.addPackage.setOnSingleClickListener {
            viewModel.packageList.observe(this) { packageList ->
                findNavController().navigate(
                    ControlPointDetailFragmentDirections.actionControlPointDetailFragmentToPackageListDialog(
                        packageList.toTypedArray(), viewModel.orderHeaderId
                    )
                )
            }
        }

        binding.updatePackage.setOnSingleClickListener {
            if (viewModel.getSelectedPackagePosition() != 0) {
                findNavController().navigate(
                    ControlPointDetailFragmentDirections.actionControlPointDetailFragmentToUpdatePackageControlDialog(
                        viewModel.selectedPackageDto!!, viewModel.selectedPackageId
                    )
                )
            } else {
                toast(getString(R.string.pleaseSelectPackage))
            }
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

        viewModel.serialCheckBox.observe(this) {
            if (binding.quantityEdt.hasFocus())
                binding.quantityEdt.hideSoftKeyboard()
        }

        viewModel.orderDetailList.observe(this) {
            adapter.submitList(it)
        }

        viewModel.packageList.observe(this) { list ->
            if (list.isNotEmpty()) {
                binding.stateView.setViewVisible(binding.packageSpinner, true)

            }
            context?.let {
                val adapter = ArrayAdapter(it, android.R.layout.simple_spinner_item, list)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.packageSpinner.adapter = adapter
            }
        }

        viewModel.scrollToPosition
            .asLiveData()
            .observe(viewLifecycleOwner) {
                toast(it)
                binding.recyclerView.post {
                    val view = binding.recyclerView.layoutManager?.findViewByPosition(it)
                    view?.let {
                        binding.nestedScrollView.smoothScrollTo(0, it.top)
                    }
                }

                binding.recyclerView.postDelayed({
                    val vh = binding.recyclerView.findViewHolderForAdapterPosition(it) as? ControlPointDetailListVH
                    vh?.animateBackground()
                }, 500)
            }
    }

    private val adapter by lazy {
        ControlPointDetailListAdapter().also {
            binding.recyclerView.adapter = it
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchEdt.requestFocus()
    }
}
