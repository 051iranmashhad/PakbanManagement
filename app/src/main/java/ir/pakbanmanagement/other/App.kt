package ir.pakbanmanagement.other

import android.content.Context
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

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
