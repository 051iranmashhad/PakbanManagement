package ir.pakbanmanagement.presentation.fragment

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentSplashBinding
import ir.pakbanmanagement.mapper.UserMapper
import ir.pakbanmanagement.other.BaseFragment
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.getColor
import ir.pakbanmanagement.other.getVersionName
import ir.pakbanmanagement.other.hasDebug
import ir.pakbanmanagement.other.isNetworkAvailable
import ir.pakbanmanagement.other.launchActivityAndFinish
import ir.pakbanmanagement.other.logV
import ir.pakbanmanagement.other.openBrowser
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.other.toMapper
import ir.pakbanmanagement.presentation.activity.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    private val mQueryParam by lazy {
        hashMapOf<String, String>()
    }

    override fun setOnView() {
        myView.apply {
            txtVersion.text = "ویرایش ${getVersionName()}"
        }

        if (hasDebug()) checkLogin()
        else launchRegister()
    }

    private fun launchRegister() {
        myView.apply {
            txtVersion.text = "ویرایش ${getVersionName()}"
            txtTitle.text = "در حال بارگذاری ..."
            txtTitle.setTextColor(getColor(R.color.color_black))
            when {
                !requireActivity().isNetworkAvailable() -> {
                    txtTitle.text = "خطا در اتصال به اینترنت!"
                    txtTitle.setTextColor(getColor(android.R.color.holo_red_dark))
                    setNavigator(ConnectionFragment())
                }

                else -> {
                    checkLogin()
                }
            }
        }
    }

    private fun getIntent() {
        val uri: Uri? = requireActivity().intent.data?.also { "$it".logV() }
        val userMapper = runBlocking {
            PrefManager.getUser.first()
        }

        when {
            userMapper.token == null
                    && uri?.getQueryParameter(Constant.Key.DATA).isNullOrEmpty() -> {
                mJob.coroutineMain {

                    delay(2000)
                    val loginUrl = "https://172.16.9.68/mobileLogin/LoginSSO"
                    loginUrl.openBrowser(requireContext())

                    delay(3000)
                    requireActivity().finishAndRemoveTask()
                }
            }

            uri != null && uri.isHierarchical -> {
                mJob.coroutineMain {
                    val userMapper = uri.getQueryParameter("data")?.toMapper<UserMapper>()
                    mQueryParam[Constant.Key.TOKEN] = "${userMapper?.token}"
                    "${uri.getQueryParameter(Constant.Key.DATA)}".logV()

                    PrefManager.setUser(userMapper)

                    checkLogin()
                }
            }
        }
    }

    private fun checkLogin() {
        mJob.coroutineMain {
            if (PrefManager.getUser.first().token.isNullOrEmpty()) getIntent()
            else {
                delay(1500)
                requireActivity().launchActivityAndFinish<MainActivity>()
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        it.finishAndRemoveTask()
    }

    override fun getViewBindingInflater(): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(layoutInflater)
    }
}
