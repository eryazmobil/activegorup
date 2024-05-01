package eryaz.software.activegroup.ui

import android.os.Bundle
import eryaz.software.activegroup.databinding.ActivityMainBinding
import eryaz.software.activegroup.ui.base.BaseActivity
import eryaz.software.activegroup.util.KeyboardEventListener
import eryaz.software.activegroup.util.StatusBarUtil

class MainActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setTranslucent(this)
        setContentView(binding.root)
        keyboardListener()
    }

    private fun keyboardListener() {
        KeyboardEventListener(
            activity = this,
            root = binding.root,
            resizeableView = binding.navHost,
            bottomView = null
        )
    }

    override fun getContentView() = binding.root
}
