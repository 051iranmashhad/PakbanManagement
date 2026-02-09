@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.presentation.main.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentHomeBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.mapper.ContractTitleValueListMapper
import ir.pakbanmanagement.mapper.UserMapper
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.LoadingDialog
import ir.pakbanmanagement.other.LocationManager
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.checkLocationAndGpsPermissions
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.gone
import ir.pakbanmanagement.other.hasGpsEnabled
import ir.pakbanmanagement.other.hasMultiplePermissionsGranted
import ir.pakbanmanagement.other.logV
import ir.pakbanmanagement.other.restartApp
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.other.toMapper
import ir.pakbanmanagement.other.toastMessage
import ir.pakbanmanagement.other.visible
import ir.pakbanmanagement.presentation.main.activity.MainActivity
import ir.pakbanmanagement.presentation.main.dialog.ListDialog
import ir.pakbanmanagement.presentation.main.viewmodel.MainViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
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

    private var mLatLng: GeoPoint? = GeoPoint(36.33356416766867, 59.50458189307316)
    private var mContractId: Int? = null

    private var mContractTitleValueListMapper: ContractTitleValueListMapper? = null

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

//                val mashhadBox = BoundingBox(36.5, 59.9, 35.9, 59.1)
//                setScrollableAreaLimitDouble(mashhadBox)
//                isHorizontalMapRepetitionEnabled = false
//                isVerticalMapRepetitionEnabled = false

//                setTileSource(getWebSDITileSource())
                setTileSource(getMashhadTileSource())

                setMultiTouchControls(true)
//                setOnTouchListener { _, _ -> true }

//                val mRotationGestureOverlay = RotationGestureOverlay(requireContext(), this)
//                mRotationGestureOverlay.isEnabled = true
//                overlays.add(mRotationGestureOverlay)

                zoomController.apply {
                    setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                }
                controller.apply {
                    setZoom(20.0)
                    setCenter(GeoPoint(36.33356416766867, 59.50458189307316))
                }

//                isTilesScaledToDpi = false
//                overlayManager.tilesOverlay.setUseDataConnection(true)

//                val provider = MapTileProviderBasic(requireContext())
//                provider.setUseDataConnection(true)
            }

            fabFindLocation.setOnClickListener {
                requireActivity().checkLocationAndGpsPermissions {
                    findLocation()
                }
            }

            btnSubmit.setOnClickListener {
                when {
                    mContractId == null -> {
                        "لطفا پیمان خود را انتخاب کنید.".toastMessage(Constant.ToastType.Info)
                    }

                    mLatLng == null -> {
                        "لطفا موقعیت خود را مشخص کنید.".toastMessage(Constant.ToastType.Info)
                        requireActivity().checkLocationAndGpsPermissions {
                            findLocation()
                        }
                    }

                    else -> setNavigator(
                        FineFragment(mLatLng, mContractId), isAddToBackStack = true
                    )
                }
            }

            (requireActivity() as MainActivity).getContractLayout().setOnClickListener {
                getContractTitleValue()
            }
        }

        mJob.coroutineMain {
            val user = mViewModel.getUser()
            if (user.contractId == null) getContractTitleValue()
            else {
                (requireActivity() as MainActivity).setContractData(
                    user.contractTitle,
                    user.contractId
                )
                mContractId = user.contractId
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
        ) findLocation()
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

                            val user = mViewModel.getUser()
                            res.contractId = user.contractId
                            res.contractTitle = user.contractTitle

                            mViewModel.setUser(res)
                        } catch (e: Exception) {
                            "$e".logV()
                        }
                    }
                }

                is Constant.ResultWrapper.Error -> {
                    when {
                        it.code == 401 -> {
                            mJob.coroutineMain {
                                PrefManager.deleteUser()
                                restartApp()
                            }
                        }

                        else -> {
//                            it.body.toastMessage()
                        }
                    }
                }
            }
        }
    }

    private fun getContractTitleValue() {
        mViewModel.getContractTitleValue(mJob) {
            when (it) {
                Constant.ResultWrapper.Loading -> {
                    mLoadingDialog.show()
                }

                is Constant.ResultWrapper.Success -> {
                    mLoadingDialog.dismiss()

                    try {
                        val res = it.body.toMapper<ContractTitleValueListMapper>()
                        mContractTitleValueListMapper = res

                        when {
                            mContractTitleValueListMapper?.data?.isEmpty() == true -> {
                                "لیست پیمان ها خالی است!".toastMessage(Constant.ToastType.Warning)
                            }

                            mContractTitleValueListMapper?.data?.size == 1 -> {
                                val item = mContractTitleValueListMapper?.data?.first()
                                mContractId = item?.value

                                mJob.coroutineMain {
                                    mViewModel.setUser(
                                        mViewModel.getUser().copy(
                                            contractTitle = item?.title,
                                            contractId = item?.value
                                        )
                                    )
                                }
                                (requireActivity() as MainActivity).setContractData(
                                    item?.title,
                                    item?.value
                                )
                            }

                            else -> {
                                val list = res.data
                                    ?.map {
                                        BottomSheetMenuMapper(
                                            title = "${it.title}",
                                            key = "${it.value}"
                                        )
                                    }
                                    ?.toMutableList() ?: mutableListOf()

                                ListDialog(
                                    context = requireContext(), list = list, isCancelable = false
                                ) { pos ->
                                    val item = mContractTitleValueListMapper?.data?.get(pos)
                                    mContractId = item?.value

                                    mJob.coroutineMain {
                                        mViewModel.setUser(
                                            mViewModel.getUser().copy(
                                                contractTitle = item?.title,
                                                contractId = item?.value
                                            )
                                        )
                                    }
                                    (requireActivity() as MainActivity).setContractData(
                                        item?.title,
                                        item?.value
                                    )
                                }.show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "خطا در دریافت پیمان!".toastMessage(Constant.ToastType.Error)
                    }
                }

                is Constant.ResultWrapper.Error -> {
                    mLoadingDialog.dismiss()
                    "خطا در دریافت پیمان!".toastMessage(Constant.ToastType.Error)
                }
            }
        }
    }

    private fun findLocation() {
        myView.apply {
            progressBar.visible()

            LocationManager.getCurrentLocation { location ->
                progressBar.gone()

                if (location == null) "خطا در دریافت موقعیت!".toastMessage(Constant.ToastType.Error)
                else {
                    mLatLng = GeoPoint(
                        location.latitude, location.longitude
                    )

                    mLatLng?.let { moveToLocation(it) }
                }
            }
        }
    }

    private fun moveToLocation(latLng: GeoPoint) {
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

    private fun getMashhadTileSource(): OnlineTileSourceBase {
        return object : OnlineTileSourceBase(
            "MashhadMap",
            0,      // Minimum Zoom
            18,     // Maximum Zoom (Adjust based on your GeoServer config)
            256,    // Tile size in pixels
            ".png",
            arrayOf("http://basemap.mashhad.ir/geoserver/gwc/service/tms/1.0.0/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                val z = getZoom(pMapTileIndex)
                val x = getX(pMapTileIndex)
                val y = getY(pMapTileIndex)

                /**
                 * GeoServer TMS uses the South-to-North Y axis.
                 * To convert standard XYZ (used by Osmdroid) to TMS,
                 * we use the formula: invertedY = (2^zoom - 1) - y
                 */
                val invertedY = (1 shl z) - 1 - y

                // Ensure the baseUrl ends with a slash if not already present
                val base = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"

                // Constructing the path based on your GeoServer layer name
                return "${base}MashhadBaseMap1401@WebMercatorQuad@png/$z/$x/$invertedY.png"
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
