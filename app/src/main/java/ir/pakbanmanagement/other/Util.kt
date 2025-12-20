@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.other

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.graphics.PorterDuff
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.Window
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import ir.pakbanmanagement.BuildConfig
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.LayoutToastMessageBinding
import ir.pakbanmanagement.presentation.main.activity.MainActivity
import ir.pakbanmanagement.presentation.main.dialog.SubmitDialog
import ir.pakbanmanagement.presentation.main.fragment.GrantLocationFragment
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.reflect.Type
import kotlin.jvm.java
import kotlin.system.exitProcess

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mSupervisorJob = SupervisorJob()
        mBinding = getViewBindingInflater()
        setContentView(myView.root)

        ViewCompat.setOnApplyWindowInsetsListener(myView.root.rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBindCreated()
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    abstract fun getViewBindingInflater(): VB

    override fun onDestroy() {
        mBinding = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroy()
    }
}

abstract class BaseActivityWithViewModel<VM : ViewModel, VB : ViewBinding>(private val mViewModelClass: Class<VM>) :
    AppCompatActivity() {

    private var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mVM: VM? = null
    protected open val mViewModel: VM get() = mVM!!
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mSupervisorJob = SupervisorJob()
        mBinding = getViewBindingInflater()
        mVM = ViewModelProvider(this@BaseActivityWithViewModel)[mViewModelClass]
        setContentView(myView.root)

        ViewCompat.setOnApplyWindowInsetsListener(myView.root.rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        onBindCreated()
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    abstract fun getViewBindingInflater(): VB

    override fun onDestroy() {
        mBinding = null
        mVM = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroy()
    }
}

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (requireActivity()::class.java.name == MainActivity::class.java.name) {
            (requireActivity() as MainActivity).setStateFragment(this)
        }
        mSupervisorJob = SupervisorJob()
        mBinding = getViewBindingInflater()
        return myView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindCreated()
        requireActivity().setOnBackPressed {
            setOnBackPressed(it)
        }
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    abstract fun setOnBackPressed(it: FragmentActivity)

    abstract fun getViewBindingInflater(): VB

    override fun onDestroyView() {
        mBinding = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroyView()
    }
}

abstract class BaseFragmentWithSavedInstanceState<VB : ViewBinding> : Fragment() {

    protected var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    protected open var mIsLoaded = false
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mSupervisorJob = SupervisorJob()
        if (!mIsLoaded) {
            mBinding = getViewBindingInflater()
        }
        //retainInstance = true
        return myView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setOnBackPressed {
            setOnBackPressed(requireActivity())
        }
        if (!mIsLoaded) {
            mIsLoaded = true
            onBindCreated()
        }
        setOnReloadView()
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    open fun setOnReloadView() {
        if (requireActivity()::class.java.name == MainActivity::class.java.name) {
            (requireActivity() as MainActivity).setStateFragment(this)
        }
    }

    abstract fun setOnBackPressed(it: FragmentActivity)

    abstract fun getViewBindingInflater(): VB

    override fun onDestroyView() {
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroyView()
    }
}

abstract class BaseFragmentWithViewModel<VM : ViewModel, VB : ViewBinding>(private val mViewModelClass: Class<VM>) :
    Fragment() {

    private var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mVM: VM? = null
    protected open val mViewModel: VM get() = mVM!!
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        if (requireActivity()::class.java.name == MainActivity::class.java.name) {
            (requireActivity() as MainActivity).setStateFragment(this)
        }
        mSupervisorJob = SupervisorJob()
        mBinding = getViewBindingInflater()
        mVM = ViewModelProvider(this@BaseFragmentWithViewModel)[mViewModelClass]
        //retainInstance = true
        return myView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBindCreated()
        requireActivity().setOnBackPressed {
            setOnBackPressed(it)
        }
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    abstract fun setOnBackPressed(it: FragmentActivity)

    abstract fun getViewBindingInflater(): VB

    override fun onDestroyView() {
        mBinding = null
        mVM = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroyView()
    }
}

abstract class BaseFragmentWithViewModelSavedInstanceState<VM : ViewModel, VB : ViewBinding>(private val mViewModelClass: Class<VM>) :
    Fragment() {

    protected var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mVM: VM? = null
    protected open val mViewModel: VM get() = mVM!!
    protected open var mIsLoaded = false
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        mSupervisorJob = SupervisorJob()
        if (!mIsLoaded) {
            mBinding = getViewBindingInflater()
        }
        mVM = ViewModelProvider(this@BaseFragmentWithViewModelSavedInstanceState)[mViewModelClass]
        //retainInstance = true
        return myView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().setOnBackPressed {
            setOnBackPressed(requireActivity())
        }

        setOnReloadView()

        if (!mIsLoaded) {
            mIsLoaded = true
            onBindCreated()
        }
    }

    open fun onBindCreated() {
        setOnView()
    }

    abstract fun setOnView()

    open fun setOnReloadView() {
        if (requireActivity()::class.java.name == MainActivity::class.java.name) {
            (requireActivity() as MainActivity).setStateFragment(this)
        }
    }

    abstract fun setOnBackPressed(it: FragmentActivity)

    abstract fun getViewBindingInflater(): VB

    override fun onDestroyView() {
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.onDestroyView()
    }
}

abstract class BaseBottomSheetDialog<VB : ViewBinding>(context: Context) :
    BottomSheetDialog(context, R.style.AppBottomSheetDialogTheme) {

    private var mBinding: VB? = null
    protected open val myView: VB get() = mBinding!!
    private var mSupervisorJob: CompletableJob? = null
    protected open val mJob: CompletableJob get() = mSupervisorJob!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSupervisorJob = SupervisorJob()
        window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        mBinding = getViewBindingInflater()
        setContentView(myView.root)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        setOnView()
    }

    abstract fun setOnView()

    abstract fun getViewBindingInflater(): VB

    override fun cancel() {
        if (mBinding != null) mBinding = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.cancel()
    }

    override fun dismiss() {
        if (mBinding != null) mBinding = null
        mSupervisorJob?.cancel()
        mSupervisorJob = null
        super.dismiss()
    }

}

internal fun getVersionName() = BuildConfig.VERSION_NAME

internal fun String.logV() {
    if (hasDebug()) Log.v("LOG_MOV", " => ${this@logV}")
}

internal fun String.logV(clazz: Class<*>) {
    if (hasDebug()) Log.v("LOG_MOV", " ${clazz.name} => ${this@logV}")
}

internal fun hasDebug(): Boolean {
    return BuildConfig.DEBUG
}

internal fun FragmentActivity.setOnBackPressed(block: (it: FragmentActivity) -> Unit) {
    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            block.invoke(this@setOnBackPressed)
        }
    }
    this.onBackPressedDispatcher.addCallback(this, callback)
}

internal fun CompletableJob.coroutineMain(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main + this + Constant.COROUTINE_EXCEPTION_HANDLER).launch {
        block.invoke(this)
    }
}

internal fun CompletableJob.coroutineDefault(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Default + this + Constant.COROUTINE_EXCEPTION_HANDLER).launch {
        block.invoke(this)
    }
}

internal fun CompletableJob.coroutineIO(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO + this + Constant.COROUTINE_EXCEPTION_HANDLER).launch {
        block.invoke(this)
    }
}

internal fun setExceptionHandler(block: (String) -> Unit) {
    if (!hasDebug()) {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    block.invoke("$e")
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            block.invoke("$t , $e")
        }
    }
}

internal fun restartApp() {
    val intent = App.appContext.packageManager.getLaunchIntentForPackage(App.appContext.packageName)
    intent?.addFlags(
        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NO_HISTORY
    )
    App.appContext.startActivity(intent)
    val activities = ActivityLifecycleManager.getActivities()
    for (activity in activities) {
        activity.finishAndRemoveTask()
    }
    Process.killProcess(Process.myPid())
    exitProcess(0)
}

internal class ActivityLifecycleManager : Application.ActivityLifecycleCallbacks {

    companion object {
        private val activityList: MutableList<Activity> = ArrayList()

        fun getActivities(): List<Activity> {
            return activityList.toList()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityList.add(activity)

//        val window = activity.window
//        val rootView = activity.findViewById<View>(android.R.id.content)
//        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
//            val systemBars = insets.getInsets(
//                WindowInsetsCompat.Type.systemBars() or
//                        WindowInsetsCompat.Type.displayCutout() or
//                        WindowInsetsCompat.Type.ime()
//            )
//            view.updatePadding(
//                top = systemBars.top,
//                left = systemBars.left,
//                right = systemBars.right,
//                bottom = systemBars.bottom
//            )
//            insets
//        }
//
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.clearFlags(
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//        )
//        window.statusBarColor = getColor(R.color.color_white)
//        window.navigationBarColor = getColor(R.color.color_white)
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityList.remove(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }
}

internal fun FragmentActivity.setNavigator(
    newFragment: Fragment,
    layoutId: Int = R.id.nav_host_fragment,
    isAddToBackStack: Boolean = false,
) {
    try {
        val manager: FragmentManager = this.supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

//        transaction.setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.setReorderingAllowed(true)
        if (isAddToBackStack) {
            val fragmentStack: Fragment? = manager.findFragmentByTag(newFragment.javaClass.name)
            if (fragmentStack == null) {
                transaction.replace(
                    layoutId, newFragment, newFragment.javaClass.name
                )
            } else {
                transaction.replace(layoutId, fragmentStack)
            }
            transaction.addToBackStack(newFragment.javaClass.name)
        } else {
            transaction.replace(
                layoutId, newFragment, newFragment.javaClass.name
            )
        }
        transaction.commit()
        /*if (this::class.java.name == MainActivity::class.java.name) {
            (this as MainActivity).setStateFragment(newFragment)
        }*/
        manager.executePendingTransactions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal fun Fragment.setNavigator(
    newFragment: Fragment,
    layoutId: Int = R.id.nav_host_fragment,
    isAddToBackStack: Boolean = false,
) {
    try {
        val manager: FragmentManager = this.requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

//        transaction.setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.setReorderingAllowed(true)
        if (isAddToBackStack) {
            val fragmentStack: Fragment? = manager.findFragmentByTag(newFragment.javaClass.name)
            if (fragmentStack == null) {
                transaction.replace(
                    layoutId, newFragment, newFragment.javaClass.name
                )
            } else {
                transaction.replace(layoutId, fragmentStack)
            }
            transaction.addToBackStack(newFragment.javaClass.name)
        } else {
            transaction.replace(
                layoutId, newFragment, newFragment.javaClass.name
            )
        }
        transaction.commit()
        /*if (this::class.java.name == MainActivity::class.java.name) {
            (this as MainActivity).setStateFragment(newFragment)
        }*/
        manager.executePendingTransactions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal fun FragmentActivity.setNavigator2(
    newFragment: Fragment,
    layoutId: Int = R.id.nav_host_fragment,
    isAddToBackStack: Boolean = false,
) {
    try {
        val manager: FragmentManager = this.supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

//        transaction.setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.setReorderingAllowed(true)
        if (isAddToBackStack) {
            transaction.replace(layoutId, newFragment)
            transaction.addToBackStack(newFragment.javaClass.name)
        } else {
            transaction.replace(
                layoutId, newFragment, newFragment.javaClass.name
            )
        }
        transaction.commitAllowingStateLoss()
        manager.executePendingTransactions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal fun Fragment.setNavigator2(
    newFragment: Fragment,
    layoutId: Int = R.id.nav_host_fragment,
    isAddToBackStack: Boolean = false,
) {
    try {
        val manager: FragmentManager = this.requireActivity().supportFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()

//        transaction.setCustomAnimations(
//            R.anim.enter_from_right,
//            R.anim.exit_to_left,
//            R.anim.enter_from_left,
//            R.anim.exit_to_right
//        )

//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.setReorderingAllowed(true)
        if (isAddToBackStack) {
            transaction.replace(layoutId, newFragment)
            transaction.addToBackStack(newFragment.javaClass.name)
        } else {
            transaction.replace(
                layoutId, newFragment, newFragment.javaClass.name
            )
        }
        transaction.commitAllowingStateLoss()
        manager.executePendingTransactions()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

internal fun Fragment.popBackStack() {
    this@popBackStack.requireActivity().supportFragmentManager.popBackStack()
}

internal fun setThemeMode(isNightMode: Boolean = false) {
    if (!isNightMode) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}

internal fun Context.setInstallSSL() {
    try {
        ProviderInstaller.installIfNeeded(this)
    } catch (e: GooglePlayServicesRepairableException) {
        GoogleApiAvailability.getInstance().showErrorNotification(this, e.connectionStatusCode)
    } catch (e: GooglePlayServicesNotAvailableException) {
        e.printStackTrace()
    }
}

internal inline fun <reified T : FragmentActivity> FragmentActivity.launchActivityAndFinish() {
    startActivity(Intent(this@launchActivityAndFinish, T::class.java))
    this@launchActivityAndFinish.finish()
}

internal inline fun <reified T : FragmentActivity> FragmentActivity.launchActivity() {
    startActivity(Intent(this@launchActivity, T::class.java))
}

internal fun getColor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(App.appContext, color)
}

internal fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        this@isNetworkAvailable.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}

internal fun View.setRoundedCorners(radiusPx: Float) {
    clipToOutline = true
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radiusPx)
        }
    }
    post {
        invalidateOutline()
    }
}

internal fun Float.toPx(): Float = this * Resources.getSystem().displayMetrics.density

internal inline fun <reified MODEL> String.toMapper(): MODEL {
    val type: Type = object : TypeToken<MODEL?>() {}.type
    val builder = GsonBuilder()
    builder.serializeNulls()
    builder.setLenient()
    val gson = builder.setPrettyPrinting().create()
    return gson.fromJson(this@toMapper, type)
}

internal inline fun <reified MODEL> Any.fromMapperTypeToken(): String {
    val type: Type = object : TypeToken<MODEL?>() {}.type
    val builder = GsonBuilder()
    builder.serializeNulls()
    builder.setLenient()
    val gson = builder.setPrettyPrinting().create()
    return gson.toJson(this@fromMapperTypeToken, type).also { it.logV() }
}

internal fun Any.fromMapper(): String {
    val type: Type = object : TypeToken<Any?>() {}.type
    val builder = GsonBuilder()
    builder.serializeNulls()
    builder.setLenient()
    val gson = builder.setPrettyPrinting().create()
    return gson.toJson(this@fromMapper, type).also { it.logV() }
}

internal fun Any.withoutNullFromMapper(): String {
    val type: Type = object : TypeToken<Any?>() {}.type
    val builder = GsonBuilder()
//    builder.serializeNulls()
    builder.setLenient()
    val gson = builder.setPrettyPrinting().create()
    return gson.toJson(this@withoutNullFromMapper, type).also { it.logV() }
}

internal fun CharSequence?.ifNullOrEmpty(block: () -> String): String {
    return if (this@ifNullOrEmpty.isNullOrEmpty()) {
        block.invoke()
    } else {
        this@ifNullOrEmpty.toString()
    }
}

internal fun Any?.ifNullOrEmpty(block: () -> String): String {
    return if (this@ifNullOrEmpty == null || this@ifNullOrEmpty.toString().isEmpty()) {
        block.invoke()
    } else {
        this@ifNullOrEmpty.toString()
    }
}

internal fun Any?.ifNullOrEmpty(): String {
    return if (this@ifNullOrEmpty == null || this@ifNullOrEmpty.toString().isEmpty()) {
        "_"
    } else {
        this@ifNullOrEmpty.toString()
    }
}

internal fun String.openBrowser(context: Context) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(this))
    context.startActivity(browserIntent)
}

@SuppressLint("InflateParams")
internal fun String?.toastMessage(type: Constant.ToastType? = null) {
    if (this@toastMessage.isNullOrEmpty()) return
    val message = this@toastMessage
    val context = App.appContext

    val layoutToast = LayoutToastMessageBinding.inflate(LayoutInflater.from(context))
    val fontFamily = ResourcesCompat.getFont(context, R.font.medium)

    val rootView = layoutToast.root
    layoutToast.apply {
        rootView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )

        txtToast.apply {
            typeface = fontFamily
            text = message
        }
        when (type) {
            Constant.ToastType.Success -> {
                layoutRoot.setBackgroundColor(getColor(R.color.color_success))
                txtToast.setTextColor(Color.WHITE)
            }

            Constant.ToastType.Warning -> {
                layoutRoot.setBackgroundColor(getColor(R.color.color_warning))
                txtToast.setTextColor(Color.BLACK)
            }

            Constant.ToastType.Error -> {
                layoutRoot.setBackgroundColor(getColor(R.color.color_error))
                txtToast.setTextColor(Color.WHITE)
            }

            Constant.ToastType.Info -> {
                layoutRoot.setBackgroundColor(getColor(R.color.color_info))
                txtToast.setTextColor(Color.WHITE)
            }

            else -> {
                layoutRoot.setBackgroundColor(getColor(R.color.color_gray_dark))
                txtToast.setTextColor(Color.WHITE)
            }
        }

        rootView.translationY = -200f
        val slideDown = ObjectAnimator.ofPropertyValuesHolder(
            rootView,
            PropertyValuesHolder.ofFloat(View.TRANSLATION_Y, -200f, 0f),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f, 1f)
        ).apply {
            duration = 300
            interpolator = DecelerateInterpolator()
        }

        val toast = Toast(context).apply {
            view = rootView
            duration = Toast.LENGTH_LONG
            setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
        }

        slideDown.start()
        toast.show()
    }
}

internal fun hasGpsEnabled(): Boolean {
    val locationManager =
        App.appContext.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager

    return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

internal fun hasSinglePermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        App.appContext, permission
    ) == PackageManager.PERMISSION_GRANTED
}

internal fun hasMultiplePermissionsGranted(permissions: List<String>): Boolean {
    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(
            App.appContext, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

internal fun FragmentActivity.checkLocationAndGpsPermissions(
    dismiss: () -> Unit = {}, block: () -> Unit
) {
    var dialog: SubmitDialog? = null
    when {
        !hasSinglePermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            dialog?.dismiss()
            dialog?.cancel()
            dialog = SubmitDialog(
                context = this@checkLocationAndGpsPermissions,
                title = "نیازمند دسترسی!",
                subTitle = "آیا مایل به فعالسازی موقعیت (Location) هستید؟",
                negativeText = "خیر",
                positiveText = "بله",
                isNegative = true,
                isCancelable = false,
                dismiss = {
                    dismiss.invoke()
                },
                block = {
                    setNavigator(GrantLocationFragment(), isAddToBackStack = true)
                })
            dialog.show()
        }

        !hasSinglePermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION) -> {
            dialog?.dismiss()
            dialog?.cancel()
            dialog = SubmitDialog(
                context = this@checkLocationAndGpsPermissions,
                title = "نیازمند دسترسی!",
                subTitle = "آیا مایل به فعالسازی موقعیت (Location) هستید؟",
                negativeText = "خیر",
                positiveText = "بله",
                isNegative = true,
                isCancelable = false,
                dismiss = {
                    dismiss.invoke()
                },
                block = {
                    setNavigator(GrantLocationFragment(), isAddToBackStack = true)
                })
            dialog.show()
        }

        !hasGpsEnabled() -> {
            dialog?.dismiss()
            dialog?.cancel()
            dialog = SubmitDialog(
                context = this@checkLocationAndGpsPermissions,
                title = "نیازمند دسترسی!",
                subTitle = "آیا مایل به فعالسازی جی پی اس (GPS) هستید؟",
                negativeText = "خیر",
                positiveText = "بله",
                isNegative = true,
                isCancelable = false,
                dismiss = {
                    dismiss.invoke()
                },
                block = {
                    setNavigator(GrantLocationFragment(), isAddToBackStack = true)
                })
            dialog.show()
        }

        else -> {
            block.invoke()
        }
    }
}

internal fun AppCompatImageView.setImageTint(color: String) {
    this@setImageTint.setColorFilter(color.toColorInt(), PorterDuff.Mode.SRC_IN)
}

internal fun AppCompatImageView.setImageTint(@ColorRes color: Int) {
    this@setImageTint.setColorFilter(getColor(color), PorterDuff.Mode.SRC_IN)
}

internal fun FragmentActivity.openLocation() {
    this@openLocation.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
}

internal fun openLocation(locationSettingsLauncher: ActivityResultLauncher<Intent>) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    locationSettingsLauncher.launch(intent)
}

internal fun FragmentActivity.openAppSetting() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", App.appContext.packageName, null)
    intent.data = uri
    this@openAppSetting.startActivity(intent)
}

internal fun Context.getColor2(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this@getColor2, color)
}

internal fun getColor(color: String): Int {
    return color.toColorInt()
}

internal fun String.toEnglishNumber(): String {
    var enNumbers = this
    val mChars = arrayOf(
        arrayOf("۰", "0"),
        arrayOf("۱", "1"),
        arrayOf("۲", "2"),
        arrayOf("۳", "3"),
        arrayOf("۴", "4"),
        arrayOf("۵", "5"),
        arrayOf("۶", "6"),
        arrayOf("۷", "7"),
        arrayOf("۸", "8"),
        arrayOf("۹", "9"),
    )
    for (num in mChars) {
        enNumbers = enNumbers.replace(num[0], num[1])
    }
    return enNumbers
}

internal open class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        setContentView(R.layout.layout_loading_dialog)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}

internal fun String?.toDecodeByteArray(): ByteArray {
    return Base64.decode(this@toDecodeByteArray, Base64.DEFAULT)
}

internal fun InputStream.encodeInputStreamToBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    val buffer = ByteArray(1024)
    var len: Int
    while (this@encodeInputStreamToBase64.read(buffer).also { len = it } != -1) {
        byteArrayOutputStream.write(buffer, 0, len)
    }
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
