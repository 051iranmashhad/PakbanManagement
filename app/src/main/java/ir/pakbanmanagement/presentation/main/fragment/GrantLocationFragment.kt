package ir.pakbanmanagement.presentation.main.fragment

import android.Manifest
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentGrantLocationBinding
import ir.pakbanmanagement.other.BaseFragment
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.PermissionManager
import ir.pakbanmanagement.other.hasGpsEnabled
import ir.pakbanmanagement.other.hasMultiplePermissionsGranted
import ir.pakbanmanagement.other.openAppSetting
import ir.pakbanmanagement.other.openLocation
import ir.pakbanmanagement.other.popBackStack
import ir.pakbanmanagement.other.setImageTint
import ir.pakbanmanagement.other.toastMessage

class GrantLocationFragment : BaseFragment<FragmentGrantLocationBinding>() {

    private val mLocationSettingsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        checkPermission()
    }

    override fun setOnView() {
        myView.apply {
            imgBack.setOnClickListener {
                popBackStack()
            }

            layoutLocation.setOnClickListener {
                val permissions = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                PermissionManager.requestMultiple(requireActivity(), permissions) { hasRequest ->
                    when (hasRequest) {
                        true -> {
                            imgLocation.setImageResource(R.drawable.ic_verified)
                            imgLocation.setImageTint(R.color.color_success)
                            imgArrowLocation.visibility = View.INVISIBLE
                        }

                        false -> {
                            imgLocation.setImageResource(R.drawable.ic_un_verified)
                            imgLocation.setImageTint(R.color.color_error)
                            imgArrowLocation.visibility = View.VISIBLE
                        }

                        else -> {
                            imgLocation.setImageResource(R.drawable.ic_un_verified)
                            imgLocation.setImageTint(R.color.color_error)
                            imgArrowLocation.visibility = View.VISIBLE

                            "دسترسی را به صورت دستی انجام دهید!".toastMessage(Constant.ToastType.Info)
                            requireActivity().openAppSetting()
                        }
                    }
                }
            }

            layoutGps.setOnClickListener {
                openLocation(mLocationSettingsLauncher)
            }

            btnSubmit.setOnClickListener {
                popBackStack()
            }
        }
    }

    private fun checkPermission() {
        // check location
        myView.apply {
            val listPermission = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (hasMultiplePermissionsGranted(listPermission)) {
                layoutLocation.isEnabled = false
                imgLocation.setImageResource(R.drawable.ic_verified)
                imgLocation.setImageTint(R.color.color_success)
                imgArrowLocation.visibility = View.INVISIBLE
            } else {
                layoutLocation.isEnabled = true
                imgLocation.setImageResource(R.drawable.ic_un_verified)
                imgLocation.setImageTint(R.color.color_error)
                imgArrowLocation.visibility = View.VISIBLE
            }

            if (hasGpsEnabled()) {
                layoutGps.isEnabled = false
                imgGps.setImageResource(R.drawable.ic_verified)
                imgGps.setImageTint(R.color.color_success)
                imgArrowGps.visibility = View.INVISIBLE
            } else {
                layoutGps.isEnabled = true
                imgGps.setImageResource(R.drawable.ic_un_verified)
                imgGps.setImageTint(R.color.color_error)
                imgArrowGps.visibility = View.VISIBLE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        popBackStack()
    }

    override fun getViewBindingInflater(): FragmentGrantLocationBinding {
        return FragmentGrantLocationBinding.inflate(layoutInflater)
    }
}
