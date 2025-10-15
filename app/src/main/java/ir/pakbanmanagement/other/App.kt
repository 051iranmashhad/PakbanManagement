package ir.pakbanmanagement.other

import android.content.Context
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : MultiDexApplication() {

    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        init()
    }

    private fun init() {
        appContext = applicationContext
        registerActivityLifecycleCallbacks(ActivityLifecycleManager())
        setExceptionHandler { _ ->
            restartApp()
        }
        setInstallSSL()
        setThemeMode()
    }
}
