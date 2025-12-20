package ir.pakbanmanagement.presentation.main.activity

import android.content.res.Resources
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.ActivityMainBinding
import ir.pakbanmanagement.other.BaseActivityWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.getVersionName
import ir.pakbanmanagement.other.restartApp
import ir.pakbanmanagement.other.setNavigator
import ir.pakbanmanagement.presentation.main.adapter.MenuAdapter
import ir.pakbanmanagement.presentation.main.dialog.SubmitDialog
import ir.pakbanmanagement.presentation.main.fragment.HomeFragment
import ir.pakbanmanagement.presentation.main.fragment.ProfileFragment
import ir.pakbanmanagement.presentation.main.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity :
    BaseActivityWithViewModel<MainViewModel, ActivityMainBinding>(MainViewModel::class.java) {

    private lateinit var mMenuAdapter: MenuAdapter

    override fun setOnView() {
        myView.apply {
            txtVersion.text = "نسخه  ${getVersionName()}"

            imgMenu.setOnClickListener {
                layoutDrawer.openDrawer(Gravity.RIGHT)
            }
        }

        setNavigator(HomeFragment())
        setBackStackHandler()
        setNavDrawer()
    }

    private fun setNavDrawer() {
        myView.apply {
            navView.layoutParams = DrawerLayout.LayoutParams(
                ((Resources.getSystem().displayMetrics.widthPixels * 0.7).toInt()),
                DrawerLayout.LayoutParams.MATCH_PARENT,
                Gravity.RIGHT
            )

            mMenuAdapter = MenuAdapter { item ->
                layoutDrawer.closeDrawer(Gravity.RIGHT)

                when (item) {
                    Constant.MenuKey.PROFILE -> {
                        setNavigator(ProfileFragment(), isAddToBackStack = true)
                    }

                    Constant.MenuKey.LOG_OUT -> {
                        SubmitDialog(
                            context = this@MainActivity,
                            title = "خروج از حساب!",
                            subTitle = "آیا مایل به خروج از حساب کاربری خود هستید؟",
                        ) {
                            mJob.coroutineMain {
                                PrefManager.deleteUser()
                                restartApp()
                            }
                        }.show()
                    }
                }
            }

            recyclerViewMenu.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager =
                    LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                adapter = mMenuAdapter
            }
        }
    }

    internal fun isDrawerOpened(): Boolean {
        return myView.layoutDrawer.isDrawerOpen(Gravity.RIGHT)
    }

    internal fun closeDrawer() {
        myView.layoutDrawer.closeDrawer(Gravity.RIGHT)
    }

    private fun setBackStackHandler() {
        mJob.coroutineMain {
            mViewModel.fragmentStateFlow.collectLatest {
                myView.apply {
                    when (it.javaClass.name) {
                        HomeFragment::class.java.name -> {
                            layoutToolBar.isVisible = true
                            layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                        }

                        else -> {
                            layoutToolBar.isVisible = false
                            layoutDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                        }
                    }
                }
            }
        }
    }

    internal fun setStateFragment(fragment: Fragment) {
        mViewModel.setStateFragment(fragment)
    }

    override fun getViewBindingInflater(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}
