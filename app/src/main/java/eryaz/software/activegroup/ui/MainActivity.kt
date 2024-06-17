package eryaz.software.activegroup.ui

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.databinding.ActivityMainBinding
import eryaz.software.activegroup.ui.base.BaseActivity
import eryaz.software.activegroup.util.KeyboardEventListener
import eryaz.software.activegroup.util.StatusBarUtil
import eryaz.software.activegroup.util.extensions.findNavHostNavController

class MainActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val navController by lazy(LazyThreadSafetyMode.NONE) {
        findNavHostNavController(R.id.nav_host)
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

    override fun onUserLeaveHint() {
        if (SessionManager.appIsLocked)
            startActivity(Intent(this, MainActivity::class.java))
        super.onUserLeaveHint()
    }

    override fun onBackPressed() {
        navController.popBackStack()
    }

    override fun getContentView() = binding.root
}
