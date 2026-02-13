@file:Suppress("RemoveSingleExpressionStringTemplate")

package ir.pakbanmanagement.presentation.main.fragment

import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.FragmentHistoryFineListBinding
import ir.pakbanmanagement.mapper.HistoryFineListMapper
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.LoadingDialog
import ir.pakbanmanagement.other.PrefManager
import ir.pakbanmanagement.other.dialog.DatePickerDialog
import ir.pakbanmanagement.other.getLocalDate
import ir.pakbanmanagement.other.ifNullOrEmpty
import ir.pakbanmanagement.other.persianToGregorian2
import ir.pakbanmanagement.other.popBackStack
import ir.pakbanmanagement.other.toMapper
import ir.pakbanmanagement.other.toastMessage
import ir.pakbanmanagement.presentation.main.adapter.FineListAdapter
import ir.pakbanmanagement.presentation.main.viewmodel.MainViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class HistoryFineListFragment :
    BaseFragmentWithViewModel<MainViewModel, FragmentHistoryFineListBinding>(MainViewModel::class.java) {

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }
    private val mQueryParam by lazy {
        hashMapOf<String, String>()
    }
    private val mUser by lazy {
        return@lazy runBlocking { PrefManager.getUser.first() }
    }

    private lateinit var mFineListAdapter: FineListAdapter

    override fun setOnView() {
        myView.apply {
            imgBack.setOnClickListener {
                popBackStack()
            }

            val dateNow = getLocalDate()
            btnDate.apply {
                text = dateNow
                tag = dateNow.persianToGregorian2("yyyy-MM-dd")
                setOnClickListener {
                    DatePickerDialog(
                        context = requireActivity(),
                        initialDate = dateNow
                    ) { date ->
                        text = date
                        tag = date.persianToGregorian2("yyyy-MM-dd")

                        getDetailByContractId()
                    }.show()
                }
            }

            mFineListAdapter = FineListAdapter()

            recyclerView.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = mFineListAdapter
            }
        }

        getDetailByContractId()
    }

    private fun getDetailByContractId() {
        mQueryParam[Constant.Key.ID] = "${mUser.contractId}"
        mQueryParam[Constant.Key.DATE] = myView.btnDate.tag.toString()
        mViewModel.getDetailByContractId(mJob, mQueryParam) {
            when (it) {
                Constant.ResultWrapper.Loading -> {
                    mLoadingDialog.show()
                }

                is Constant.ResultWrapper.Success -> {
                    mLoadingDialog.dismiss()

                    val res = it.body.toMapper<HistoryFineListMapper>()
                    try {
                        if (res.success == true) {
                            val list =
                                res.data?.supSepcialFineDetails?.toMutableList() ?: mutableListOf()
                            mFineListAdapter.setList(list)
                        } else {
                            "${res.message.ifNullOrEmpty { "خطا در دریافت اطلاعات!" }}".toastMessage(
                                Constant.ToastType.Warning
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()

                        "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                        popBackStack()
                    }
                }

                is Constant.ResultWrapper.Error -> {
                    mLoadingDialog.dismiss()

                    "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                    popBackStack()
                }
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        popBackStack()
    }

    override fun getViewBindingInflater(): FragmentHistoryFineListBinding {
        return FragmentHistoryFineListBinding.inflate(layoutInflater)
    }
}
