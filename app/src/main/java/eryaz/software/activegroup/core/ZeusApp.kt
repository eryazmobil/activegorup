package eryaz.software.activegroup.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.multidex.BuildConfig
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import eryaz.software.activegroup.data.di.appModuleApis
import eryaz.software.activegroup.data.di.appModuleRepos
import eryaz.software.activegroup.data.persistence.SessionManager
import eryaz.software.activegroup.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ZeusApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()

        installUncaughtExceptionToastHandler()
        initKoin()
        init()
    }

    private fun init() {
        SessionManager.init(applicationContext)

            Timber.plant(Timber.DebugTree())
    }

    private fun initKoin() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.ERROR else Level.NONE)
            androidContext(this@ZeusApp)
            androidFileProperties()
            modules(listOf(appModule, appModuleApis, appModuleRepos))
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun installUncaughtExceptionToastHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val message = buildCrashToastMessage(throwable)
                if (thread === Looper.getMainLooper().thread) {
                    try {
                        Toast.makeText(
                            applicationContext,
                            message,
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (_: Exception) {
                    }
                    Thread.sleep(2800)
                } else {
                    val latch = CountDownLatch(1)
                    Handler(Looper.getMainLooper()).post {
                        try {
                            Toast.makeText(
                                applicationContext,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        } catch (_: Exception) {
                        } finally {
                            latch.countDown()
                        }
                    }
                    latch.await(3, TimeUnit.SECONDS)
                }
            } catch (_: Exception) {
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun buildCrashToastMessage(t: Throwable): String {
        val sb = StringBuilder()
        sb.append(t.javaClass.simpleName)
        t.message?.takeIf { it.isNotBlank() }?.let { sb.append(": ").append(it) }
        var cause = t.cause
        var depth = 0
        while (cause != null && depth < 4) {
            sb.append(" ← ")
            sb.append(cause.javaClass.simpleName)
            cause.message?.takeIf { it.isNotBlank() }?.let { sb.append(": ").append(it) }
            cause = cause.cause
            depth++
        }
        val full = sb.toString()
        return if (full.length > 450) full.substring(0, 447) + "…" else full
    }
}