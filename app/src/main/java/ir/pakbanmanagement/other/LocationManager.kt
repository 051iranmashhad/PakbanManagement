@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.other

import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationManager {

    private val mFusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(App.appContext)
    }
    private var mLocationCallback: LocationCallback? = null

    internal fun getCurrentLocation(block: (Location?) -> Unit) {
        mFusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                block.invoke(location)
            } else {
                getAccurateSingleLocation(block)
            }
        }.addOnFailureListener {
            block.invoke(null)
        }
    }

    private fun getAccurateSingleLocation(block: (Location?) -> Unit) {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L).setMaxUpdates(1).build()

        val singleCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                block(result.lastLocation)
                mFusedLocationClient.removeLocationUpdates(this)
            }
        }

        mFusedLocationClient.requestLocationUpdates(
            locationRequest, singleCallback, Looper.getMainLooper()
        )
    }

    internal fun removeCurrentLocation() {
        mLocationCallback?.let {
            mFusedLocationClient.removeLocationUpdates(it)
            mLocationCallback = null
        }
    }
}
