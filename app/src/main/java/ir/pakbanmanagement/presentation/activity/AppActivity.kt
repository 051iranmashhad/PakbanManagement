package ir.pakbanmanagement.presentation.activity

import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.ActivityAppBinding
import ir.pakbanmanagement.other.BaseActivity
import ir.pakbanmanagement.other.logV
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.presentation.fragment.SplashFragment

@AndroidEntryPoint
class AppActivity : BaseActivity<ActivityAppBinding>() {

    override fun setOnView() {
//        val data = intent.data?.getQueryParameter("data")
//        "$data".logV()


        setNavigator(SplashFragment())
    }

    override fun getViewBindingInflater(): ActivityAppBinding {
        return ActivityAppBinding.inflate(layoutInflater)
    }
}
