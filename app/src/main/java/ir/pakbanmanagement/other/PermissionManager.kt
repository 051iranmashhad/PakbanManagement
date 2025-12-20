package ir.pakbanmanagement.other

import androidx.fragment.app.FragmentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import kotlin.let

object PermissionManager {

    internal fun requestSingle(
        activity: FragmentActivity, permission: String, block: (Boolean?) -> Unit
    ) {
        Dexter.withContext(activity)
            .withPermission(permission)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    block.invoke(true)
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    block.invoke(false)
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?, p1: PermissionToken?
                ) {
                    block.invoke(null)
                }
            })
            .withErrorListener {
                block.invoke(null)
            }
            .check()
    }

    internal fun requestMultiple(
        activity: FragmentActivity, permissions: List<String>, block: (Boolean?) -> Unit
    ) {
        Dexter.withContext(activity)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) block.invoke(true)
                        else block.invoke(false)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: List<PermissionRequest?>?,
                    p1: PermissionToken?
                ) {
                    block.invoke(null)
                }
            })
            .withErrorListener {
                block.invoke(null)
            }
            .check()
    }
}
