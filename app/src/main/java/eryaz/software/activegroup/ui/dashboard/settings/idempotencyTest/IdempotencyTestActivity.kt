package eryaz.software.activegroup.ui.dashboard.settings.idempotencyTest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import eryaz.software.activegroup.databinding.ActivityIdempotencyTestBinding
import eryaz.software.activegroup.ui.base.BaseActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class IdempotencyTestActivity : BaseActivity() {

    private val viewModel by viewModel<IdempotencyTestVM>()

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityIdempotencyTestBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.clientIdInput.setText(DEFAULT_CLIENT_ID)
        binding.intervalInput.setText(DEFAULT_INTERVAL_MS)

        binding.startStopButton.setOnClickListener {
            if (viewModel.isRunning.value) {
                viewModel.stop()
                return@setOnClickListener
            }

            val clientId = binding.clientIdInput.text.toString().toIntOrNull()
            val interval = binding.intervalInput.text.toString().toLongOrNull()

            if (clientId == null || interval == null || interval <= 0) {
                Toast.makeText(this, "Geçerli clientId ve aralık girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.logView.text = ""
            binding.errorBanner.visibility = View.GONE
            viewModel.start(clientId, interval)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRunning.collect { running ->
                    binding.startStopButton.text = if (running) "Durdur" else "Başlat"
                    binding.clientIdInput.isEnabled = !running
                    binding.intervalInput.isEnabled = !running
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logLines.collect { line ->
                    appendLog(line)
                }
            }
        }
    }

    private fun appendLog(line: IdempotencyLogLine) {
        val shortKey = if (line.idempotencyKey.length > 8)
            line.idempotencyKey.take(8) + "…"
        else
            line.idempotencyKey

        binding.logView.append(
            "#${line.index} [${line.timestamp}] key=$shortKey -> ${line.resultText}\n"
        )
        binding.logScroll.post {
            binding.logScroll.fullScroll(View.FOCUS_DOWN)
        }

        if (line.isError) {
            binding.errorBanner.text = line.errorMessage?.takeIf { it.isNotBlank() }
                ?: "Bilinmeyen hata"
            binding.errorBanner.visibility = View.VISIBLE
        }
    }

    override fun getContentView() = binding.root

    companion object {
        private const val DEFAULT_CLIENT_ID = "4365"
        private const val DEFAULT_INTERVAL_MS = "1000"
    }
}
