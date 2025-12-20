package ir.pakbanmanagement.presentation.main.activity

import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.ActivityAppBinding
import ir.pakbanmanagement.other.BaseActivity
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.presentation.main.fragment.SplashFragment

@AndroidEntryPoint
class AppActivity : BaseActivity<ActivityAppBinding>() {

    override fun setOnView() {
        setNavigator(SplashFragment())
    }

    override fun getViewBindingInflater(): ActivityAppBinding {
        return ActivityAppBinding.inflate(layoutInflater)
    }
}
