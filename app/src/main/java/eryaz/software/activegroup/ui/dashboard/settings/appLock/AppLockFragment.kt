package eryaz.software.activegroup.ui.dashboard.settings.appLock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.databinding.FragmentAppLockBinding
import eryaz.software.activegroup.ui.base.BaseBottomSheetDialogFragmentKt
import org.koin.androidx.viewmodel.ext.android.viewModel

class AppLockFragment : BaseBottomSheetDialogFragmentKt() {
    override val viewModel by viewModel<AppLockVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentAppLockBinding.inflate(layoutInflater)
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
        viewModel.appLockStatusChanged
            .asLiveData()
            .observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }

        viewModel.wrongPassword
            .asLiveData()
            .observe(viewLifecycleOwner) {

            }
    }

    override fun backgroundRounded()=false
}