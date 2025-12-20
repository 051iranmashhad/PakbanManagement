package ir.pakbanmanagement.presentation.main.fragment

import android.net.Uri
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentSplashBinding
import ir.pakbanmanagement.mapper.TokenMapper
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
import ir.pakbanmanagement.presentation.main.activity.MainActivity
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
            when {
                !requireActivity().isNetworkAvailable() -> {
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
        val tokenMapper = runBlocking {
            PrefManager.getToken.first()
        }

        when {
            tokenMapper.token.isNullOrEmpty() && uri?.getQueryParameter(Constant.Key.DATA)
                .isNullOrEmpty() -> {
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
                    val tokenMapper = uri.getQueryParameter("data")?.toMapper<TokenMapper>()
                    mQueryParam[Constant.Key.TOKEN] = "${tokenMapper?.token}"
                    "${uri.getQueryParameter(Constant.Key.DATA)}".logV()

                    PrefManager.setToken(tokenMapper)

                    checkLogin()
                }
            }
        }
    }

    private fun checkLogin() {
        mJob.coroutineMain {
            if (PrefManager.getToken.first().token.isNullOrEmpty()) getIntent()
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
