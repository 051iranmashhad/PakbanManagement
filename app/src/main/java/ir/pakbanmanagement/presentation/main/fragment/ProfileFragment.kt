package ir.pakbanmanagement.presentation.main.fragment

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.FragmentProfileBinding
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.base.BaseViewModel
import ir.pakbanmanagement.other.ifNullOrEmpty
import ir.pakbanmanagement.other.popBackStack
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class ProfileFragment :
    BaseFragmentWithViewModel<BaseViewModel, FragmentProfileBinding>(BaseViewModel::class.java) {

    private val mUser by lazy {
        return@lazy runBlocking { PrefManager.getUser.first() }
    }

    override fun setOnView() {
        myView.apply {

            imgBack.setOnClickListener {
                popBackStack()
            }

            txtFullName.text = buildString {
                append(mUser.data?.firstName.ifNullOrEmpty())
                append(" ")
                append(mUser.data?.lastName.ifNullOrEmpty())
            }
            txtUserName.text = mUser.data?.username.ifNullOrEmpty()
            txtMobileNumber.text = mUser.data?.mobileNum.ifNullOrEmpty()

            btnSubmit.setOnClickListener {
                popBackStack()
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        popBackStack()
    }

    override fun getViewBindingInflater(): FragmentProfileBinding {
        return FragmentProfileBinding.inflate(layoutInflater)
    }
}
