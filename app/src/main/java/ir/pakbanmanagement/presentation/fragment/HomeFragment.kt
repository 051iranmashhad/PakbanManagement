@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.presentation.fragment

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.FragmentHomeBinding
import ir.pakbanmanagement.other.BaseFragment
import ir.pakbanmanagement.presentation.activity.MainActivity
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.util.MapTileIndex.getX
import org.osmdroid.util.MapTileIndex.getY
import org.osmdroid.util.MapTileIndex.getZoom
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    override fun setOnView() {
        myView.apply {
            run {
                val customTileSource = object : OnlineTileSourceBase(
                    "MashhadCustomMap",
                    0,
                    18,
                    256,
                    ".png",
                    arrayOf("https://websdi.mashhad.ir/api/embed/basemaps/57/proxy/")
                ) {
                    override fun getTileURLString(pMapTileIndex: Long): String {
                        val zoom = getZoom(pMapTileIndex)
                        val x = getX(pMapTileIndex)
                        val y = getY(pMapTileIndex)

                        val invertedY = (1 shl zoom) - 1 - y
                        val token = "5f1db2d3-f5c1-419a-9670-5407f4c90abb"

                        return "$baseUrl?token=$token&x=$x&y=$invertedY&z=$zoom"
                    }
                }

                mapView.apply {
                    setUseDataConnection(true)
                    setTileSource(customTileSource)
                    setMultiTouchControls(true)

                    zoomController.apply {
                        setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    }
                    controller.apply {
                        setZoom(15.0)
                        setCenter(GeoPoint(36.3000, 59.6000))
                    }
                }

                val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), mapView)
                mRotationGestureOverlay.isEnabled = true
                mapView.overlays.add(mRotationGestureOverlay)
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        if (!(requireActivity() as MainActivity).isDrawerOpened()) it.finishAndRemoveTask()
        else (requireActivity() as MainActivity).closeDrawer()
    }

    override fun getViewBindingInflater(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }
}
