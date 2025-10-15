@file:Suppress("DEPRECATION")

package ir.pakbanmanagement.other

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Outline
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
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
import ir.pakbanmanagement.presentation.activity.MainActivity
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.reflect.Type
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
    android.os.Process.killProcess(Process.myPid())
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

