package eryaz.software.activegroup.ui.dashboard.settings.changePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import eryaz.software.activegroup.databinding.FragmentChagePasswordBinding
import eryaz.software.activegroup.ui.base.BaseDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordDialogFragment : BaseDialogFragment() {
    override val viewModel by viewModel<ChangePasswordVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        FragmentChagePasswordBinding.inflate(layoutInflater)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.passwordMatching
            .observe(viewLifecycleOwner) { isMatching ->
                if (isMatching) {
                    binding.againPassword.error = null
                } else {
                    binding.againPassword.error = "Girdiler eşleşmiyor."
                }
            }
        viewModel.wasChangedPassword
            .observe(viewLifecycleOwner) {
                findNavController().navigateUp()
                Toast.makeText(context, "Şifre değiştirildi.", Toast.LENGTH_SHORT).show()
            }
    }
}
