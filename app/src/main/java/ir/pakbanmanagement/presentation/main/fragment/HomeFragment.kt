@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.presentation.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentHomeBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.mapper.UserMapper
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.LoadingDialog
import ir.pakbanmanagement.other.LocationManager
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.checkLocationAndGpsPermissions
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.hasGpsEnabled
import ir.pakbanmanagement.other.hasMultiplePermissionsGranted
import ir.pakbanmanagement.other.logV
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.other.toMapper
import ir.pakbanmanagement.other.toastMessage
import ir.pakbanmanagement.presentation.main.activity.MainActivity
import ir.pakbanmanagement.presentation.main.dialog.FineChildListDialog
import ir.pakbanmanagement.presentation.main.dialog.FineListDialog
import ir.pakbanmanagement.presentation.main.dialog.ListDialog
import ir.pakbanmanagement.presentation.main.viewmodel.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex.getX
import org.osmdroid.util.MapTileIndex.getY
import org.osmdroid.util.MapTileIndex.getZoom
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File

@AndroidEntryPoint
class HomeFragment :
    BaseFragmentWithViewModel<MainViewModel, FragmentHomeBinding>(MainViewModel::class.java) {

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }
    private val mQueryParam by lazy {
        hashMapOf<String, String>()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun setOnView() {
        myView.apply {

            mapView.apply {
                System.setProperty("http.keepAlive", "true")
                System.setProperty("http.maxConnections", "20")

                val osmDir = File(requireContext().cacheDir, "osmdroid")
                if (!osmDir.exists()) osmDir.mkdirs()

                Configuration.getInstance().apply {
                    osmdroidBasePath = osmDir
                    osmdroidTileCache = File(osmDir, "tiles")
                    userAgentValue =
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36"
                    cacheMapTileCount = 20
                    cacheMapTileOvershoot = 10
                    tileFileSystemCacheMaxBytes = 300L * 1024 * 1024
                    tileFileSystemCacheTrimBytes = 250L * 1024 * 1024
                    tileDownloadThreads = 6
                    tileFileSystemThreads = 4
                    tileFileSystemMaxQueueSize = 40
                }

                setUseDataConnection(true)
                setTileSource(getWebSDITileSource())
                setMultiTouchControls(true)
                setOnTouchListener { _, _ -> true }

                val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), this)
                mRotationGestureOverlay.isEnabled = true
                overlays.add(mRotationGestureOverlay)

                zoomController.apply {
                    setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                }
                controller.apply {
                    setZoom(20.0)
                    setCenter(GeoPoint(36.33356416766867, 59.50458189307316))
                }

                isTilesScaledToDpi = false
                overlayManager.tilesOverlay.setUseDataConnection(true)

                val provider = MapTileProviderBasic(requireContext())
                provider.setUseDataConnection(true)
            }

            fabFindLocation.setOnClickListener {
                requireActivity().checkLocationAndGpsPermissions {
                    findLocation()
                }
            }

            btnSubmit.setOnClickListener {
                val list = mutableListOf<BottomSheetMenuMapper>().apply {
                    add(BottomSheetMenuMapper(title = "ثبت جریمه"))
                }
                ListDialog(
                    context = requireContext(), list = list
                ) { pos ->
                    when (pos) {
                        0 -> {
                            getPriceListSeason { res ->
                                var fineDialog: FineListDialog? = null

                                fineDialog = FineListDialog(
                                    requireContext(),
                                    mJob,
                                    res.data?.toMutableList() ?: mutableListOf()
                                ) { parent ->
                                    if (parent.priceListRowDTOs?.isEmpty() == true) {
                                        setNavigator(
                                            FineFragment(priceListSeasonDataMapper = parent),
                                            isAddToBackStack = true
                                        )
                                        fineDialog?.dismiss()
                                    } else {
                                        "زیر شاخه | ${parent.title}".toastMessage(Constant.ToastType.Info)
                                        FineChildListDialog(
                                            requireContext(),
                                            mJob,
                                            parent.priceListRowDTOs?.toMutableList()
                                                ?: mutableListOf()
                                        ) { child ->
                                            setNavigator(
                                                FineFragment(
                                                    priceListSeasonDataPriceRowDTOsMapper = child
                                                ), isAddToBackStack = true
                                            )
                                            fineDialog?.dismiss()
                                        }.show()
                                    }
                                }
                                fineDialog.show()
                            }
                        }
                    }
                }.show()
            }
        }

        setPingAnimation()
        getUserInfo()
    }

    override fun onResume() {
        super.onResume()
        if (hasMultiplePermissionsGranted(
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) && hasGpsEnabled()
        ) {
            findLocation()
        }
    }

    private fun getUserInfo() {
        mViewModel.getUserInfo(mJob) {
            when (it) {
                Constant.ResultWrapper.Loading -> {

                }

                is Constant.ResultWrapper.Success -> {
                    mJob.coroutineMain {
                        try {
                            val res = it.body.toMapper<UserMapper>()
                            PrefManager.setUser(res)
                        } catch (e: Exception) {
                            "$e".logV()
                        }
                    }
                }

                is Constant.ResultWrapper.Error -> {

                }
            }
        }
    }

    private fun getPriceListSeason(block: (PriceListSeasonMapper) -> Unit) {
        mViewModel.getPriceListSeason(mJob, mQueryParam) {
            when (it) {
                Constant.ResultWrapper.Loading -> {
                    mLoadingDialog.show()
                }

                is Constant.ResultWrapper.Success -> {
                    mLoadingDialog.dismiss()

                    val res = it.body.toMapper<PriceListSeasonMapper>()

                    if (res.success == true) block.invoke(res)
                    else "لیست جریمه ها دریافت نشد!".toastMessage(Constant.ToastType.Warning)
                }

                is Constant.ResultWrapper.Error -> {
                    mLoadingDialog.dismiss()
                    "حطا در دریافت اطلاعات | ${it.code}".toastMessage()
                }
            }
        }
    }

    private fun findLocation() {
        myView.apply {
            progressBar.isVisible = true

            LocationManager.getCurrentLocation { location ->
                progressBar.isVisible = false

                location?.let {
                    val latLng = GeoPoint(
                        location.latitude, location.longitude
                    )
//                    val latLng = GeoPoint(36.33356416766867, 59.50458189307316)

                    movetToLocation(latLng)

                } ?: "خطا در دریافت موقعیت!".toastMessage(Constant.ToastType.Error)
            }
        }
    }

    private fun movetToLocation(latLng: GeoPoint) {
        myView.apply {
            mapView.controller.apply {
                setCenter(latLng)
                setZoom(19.7)
            }
        }
    }

    private fun setPingAnimation() {
        myView.apply {
            val scaleDown = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_down)
            val scaleUp = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_up)

            scaleDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    imgMarker.startAnimation(scaleUp)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            scaleUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    imgMarker.startAnimation(scaleDown)
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })

            imgMarker.startAnimation(scaleUp)

        }
    }

    private fun getWebSDITileSource(): OnlineTileSourceBase {
        val customTileSource = object : OnlineTileSourceBase(
            "MashhadCustomMap",
            10,
            25,
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

        return customTileSource
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        if (!(requireActivity() as MainActivity).isDrawerOpened()) it.finishAndRemoveTask()
        else (requireActivity() as MainActivity).closeDrawer()
    }

    override fun getViewBindingInflater(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }
}
