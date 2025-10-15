package ir.pakbanmanagement.presentation.fragment

import android.content.Intent
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import ir.pakbanmanagement.databinding.FragmentConnectionBinding
import ir.pakbanmanagement.other.BaseFragment
import ir.pakbanmanagement.other.isNetworkAvailable
import ir.pakbanmanagement.other.setNavigator

class ConnectionFragment : BaseFragment<FragmentConnectionBinding>() {

    override fun setOnView() {
        myView.apply {
            layoutWifi.setOnClickListener {
                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            }

            layoutData.setOnClickListener {
                startActivity(Intent(Settings.ACTION_DATA_ROAMING_SETTINGS))
            }

            btnRetry.setOnClickListener {
                if (requireActivity().isNetworkAvailable()) setNavigator(SplashFragment())
                else btnRetry.text = "خطا / تلاش مجدد"
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        it.finish()
    }

    override fun getViewBindingInflater(): FragmentConnectionBinding {
        return FragmentConnectionBinding.inflate(layoutInflater)
    }
}
