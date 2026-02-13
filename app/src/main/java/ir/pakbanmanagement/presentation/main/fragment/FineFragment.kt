package ir.pakbanmanagement.presentation.main.fragment

import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.databinding.FragmentFineBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.mapper.EstimateListMapper
import ir.pakbanmanagement.mapper.FinancialEndPenaltyListMapper
import ir.pakbanmanagement.mapper.ImageListMapper
import ir.pakbanmanagement.mapper.SegmentTitleValueListMapper
import ir.pakbanmanagement.mapper.SupervisionSpecialFineMapper
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.LoadingDialog
import ir.pakbanmanagement.other.dialog.DatePickerDialog
import ir.pakbanmanagement.other.dialog.TimePickerDialog
import ir.pakbanmanagement.other.fromMapper
import ir.pakbanmanagement.other.persianToGregorian
import ir.pakbanmanagement.other.popBackStack
import ir.pakbanmanagement.other.toJsonTree
import ir.pakbanmanagement.other.toMapper
import ir.pakbanmanagement.other.toastMessage
import ir.pakbanmanagement.other.uriToCompressedBase64
import ir.pakbanmanagement.presentation.main.adapter.ImageListAdapter
import ir.pakbanmanagement.presentation.main.dialog.ListDialog
import ir.pakbanmanagement.presentation.main.dialog.ListWithSearchDialog
import ir.pakbanmanagement.presentation.main.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

@AndroidEntryPoint
class FineFragment(
    private val mLatLng: GeoPoint?,
    private val mContractId: Int?,
) : BaseFragmentWithViewModel<MainViewModel, FragmentFineBinding>(MainViewModel::class.java) {

    private val mLoadingDialog: LoadingDialog by lazy {
        LoadingDialog(requireContext())
    }
    private val mQueryParam by lazy {
        hashMapOf<String, String>()
    }

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                lifecycleScope.launch {
                    val base64 = uriToCompressedBase64(requireActivity(), uri)
                    mImageListAdapter.addItem(ImageListMapper(base64Data = base64))
                }
            }
        }

    private lateinit var mImageListAdapter: ImageListAdapter

    override fun setOnView() {
        myView.apply {
            ViewCompat.setOnApplyWindowInsetsListener(myView.root) { v, insets ->

                val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
                val navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())

                val bottomPadding = (imeInsets.bottom - navInsets.bottom)
                    .coerceAtLeast(0)

                v.setPadding(
                    v.paddingLeft,
                    v.paddingTop,
                    v.paddingRight,
                    bottomPadding
                )
                insets
            }

            imgBack.setOnClickListener {
                popBackStack()
            }

            layoutSegment.apply {
                txtTitle.apply {
                    text = "انتخاب کنید"
                    tag = Constant.Key.EMPTY
                }
                root.setOnClickListener {
                    getSegmentTitleValueByContract()
                }
            }
            layoutFineType.apply {
                txtTitle.apply {
                    text = "انتخاب کنید"
                    tag = Constant.Key.EMPTY
                }
                root.setOnClickListener {
                    val list = mutableListOf(
                        BottomSheetMenuMapper(
                            title = "جریمه فهرست بها",
                            key = "1"
                        ),
                        BottomSheetMenuMapper(
                            title = "کسر از صورت وضعیت",
                            key = "2"
                        )
                    )

                    ListDialog(
                        context = requireContext(), list = list, isCancelable = false
                    ) { pos ->
                        val item = list[pos]
                        layoutFineType.txtTitle.apply {
                            text = item.title
                            tag = item.key
                        }

                        layoutFine.txtTitle.apply {
                            text = "انتخاب کنید"
                            tag = ""
                        }
                    }.show()
                }
            }
            layoutFine.apply {
                txtTitle.apply {
                    text = "انتخاب کنید"
                    tag = Constant.Key.EMPTY
                }
                root.setOnClickListener {
                    when (layoutFineType.txtTitle.tag) {
                        Constant.Key.EMPTY -> {
                            "ابتدا نوع جریمه را انتخاب کنید.".toastMessage(Constant.ToastType.Info)
                        }

                        "1" -> getEstimateDetails()
                        "2" -> getFinancialEndPenalty()
                    }
                }
            }
            layoutDate.apply {
                txtTitle.apply {
                    text = "انتخاب کنید"
                    tag = Constant.Key.EMPTY
                }
                root.setOnClickListener {
                    DatePickerDialog(
                        context = requireActivity()
                    ) { date ->
                        txtTitle.apply {
                            text = date
                            tag = date
                        }
                    }.show()
                }
            }
            layoutTime.apply {
                txtTitle.apply {
                    text = "انتخاب کنید"
                    tag = Constant.Key.EMPTY
                }
                root.setOnClickListener {
                    TimePickerDialog(
                        context = requireActivity()
                    ) { time ->
                        txtTitle.apply {
                            text = time
                            tag = time
                        }
                    }.show()
                }
            }

            mImageListAdapter = ImageListAdapter {
                mImageListAdapter.removeItem(it)
                "حذف شد".toastMessage()
            }
            recyclerView.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager = object : GridLayoutManager(requireContext(), 3, VERTICAL, false) {
                    override fun isLayoutRTL(): Boolean {
                        return true
                    }
                }
                adapter = mImageListAdapter
            }

            btnAddImage.setOnClickListener {
                imagePickerLauncher.launch("image/*")
            }

            btnSubmit.setOnClickListener {
                when {
                    mContractId == null -> {
                        "لطفا پیمان را انتخاب کنید".toastMessage(Constant.ToastType.Warning)
                        popBackStack()
                    }

                    mLatLng == null -> {
                        "لطفا موقعیت خود را مشخص کنید!".toastMessage(Constant.ToastType.Warning)
                        popBackStack()
                    }

                    layoutSegment.txtTitle.tag.toString().isEmpty() -> {
                        "لطفا محمدوه نظافت را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    layoutFineType.txtTitle.tag.toString().isEmpty() -> {
                        "لطفا نوع جریمه را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    layoutFine.txtTitle.tag.toString().isEmpty() -> {
                        "لطفا جریمه را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    edtAmount.text.toString().isEmpty() -> {
                        "لطفا مقدار (حجم) را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    layoutDate.txtTitle.tag.toString().isEmpty() -> {
                        "لطفا تاریخ را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    layoutTime.txtTitle.tag.toString().isEmpty() -> {
                        "لطفا ساعت را انتخاب کنید!".toastMessage(Constant.ToastType.Warning)
                    }

                    else -> {
                        val body = JsonObject().apply {
                            addProperty("ContractId", "$mContractId")
                            addProperty("SegmentId", "${layoutSegment.txtTitle.tag}")
                            addProperty(
                                "SupSepcialFineDetailsType",
                                "${layoutFineType.txtTitle.tag}"
                            )

                            if (layoutFineType.txtTitle.tag == "1") addProperty(
                                "EstimateDetailesId",
                                "${layoutFine.txtTitle.tag}"
                            ) else addProperty(
                                "FinancialEndPenaltyId",
                                "${layoutFine.txtTitle.tag}"
                            )

                            addProperty("Amount", edtAmount.text.toString())
                            addProperty(
                                "FineViewDate",
                                "${layoutDate.txtTitle.tag} ${layoutTime.txtTitle.tag}".persianToGregorian()
                            )
                            addProperty(
                                "SelectedPointCoordinates",
                                "${mLatLng.latitude},${mLatLng.longitude}"
                            )

                            if (mImageListAdapter.getList().isNotEmpty())
                                add(
                                    "Attachments",
                                    mImageListAdapter.getList().toJsonTree()
                                )
                        }
                        mQueryParam[Constant.Key.BODY] = body.fromMapper()
                        saveRequestSupervisionSpecialFine()
                    }
                }
            }
        }
    }

    private fun getSegmentTitleValueByContract() {
        myView.apply {
            mQueryParam[Constant.Key.ID] = "$mContractId"
            mViewModel.getSegmentTitleValueByContract(mJob, mQueryParam) {
                when (it) {
                    Constant.ResultWrapper.Loading -> {
                        mLoadingDialog.show()
                    }

                    is Constant.ResultWrapper.Success -> {
                        mLoadingDialog.dismiss()

                        try {
                            val res = it.body.toMapper<SegmentTitleValueListMapper>()

                            val list = res.data
                                ?.map {
                                    BottomSheetMenuMapper(
                                        title = "${it.title}",
                                        key = "${it.value}"
                                    )
                                }
                                ?.toMutableList() ?: mutableListOf()

                            ListWithSearchDialog(
                                context = requireContext(), list = list, isCancelable = false
                            ) { pos ->
                                val item = res.data?.get(pos)
                                layoutSegment.txtTitle.apply {
                                    text = "${item?.title}"
                                    tag = "${item?.value}"
                                }
                            }.show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                        }
                    }

                    is Constant.ResultWrapper.Error -> {
                        mLoadingDialog.dismiss()
                        "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Error)
                    }
                }
            }
        }
    }

    private fun getEstimateDetails() {
        myView.apply {
            mQueryParam[Constant.Key.ID] = "$mContractId"
            mViewModel.getEstimateDetails(mJob, mQueryParam) {
                when (it) {
                    Constant.ResultWrapper.Loading -> {
                        mLoadingDialog.show()
                    }

                    is Constant.ResultWrapper.Success -> {
                        mLoadingDialog.dismiss()

                        try {
                            val res = it.body.toMapper<EstimateListMapper>()

                            val list = res.data
                                ?.map {
                                    BottomSheetMenuMapper(
                                        title = "${it.title}",
                                        key = "${it.value}"
                                    )
                                }
                                ?.toMutableList() ?: mutableListOf()

                            ListWithSearchDialog(
                                context = requireContext(), list = list, isCancelable = false
                            ) { pos ->
                                val item = res.data?.get(pos)
                                layoutFine.txtTitle.apply {
                                    text = "${item?.title}"
                                    tag = "${item?.value}"
                                }
                            }.show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                        }
                    }

                    is Constant.ResultWrapper.Error -> {
                        mLoadingDialog.dismiss()
                        "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Error)
                    }
                }
            }
        }
    }

    private fun getFinancialEndPenalty() {
        myView.apply {
            mQueryParam[Constant.Key.ID] = "$mContractId"
            mViewModel.getFinancialEndPenalty(mJob, mQueryParam) {
                when (it) {
                    Constant.ResultWrapper.Loading -> {
                        mLoadingDialog.show()
                    }

                    is Constant.ResultWrapper.Success -> {
                        mLoadingDialog.dismiss()

                        try {
                            val res = it.body.toMapper<FinancialEndPenaltyListMapper>()

                            val list = res.data
                                ?.map {
                                    BottomSheetMenuMapper(
                                        title = "${it.title}",
                                        key = "${it.value}"
                                    )
                                }
                                ?.toMutableList() ?: mutableListOf()

                            ListWithSearchDialog(
                                context = requireContext(), list = list, isCancelable = false
                            ) { pos ->
                                val item = res.data?.get(pos)
                                layoutFine.txtTitle.apply {
                                    text = "${item?.title}"
                                    tag = "${item?.value}"
                                }
                            }.show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                        }
                    }

                    is Constant.ResultWrapper.Error -> {
                        mLoadingDialog.dismiss()
                        "خطا در دریافت اطلاعات!".toastMessage(Constant.ToastType.Error)
                    }
                }
            }
        }
    }

    private fun saveRequestSupervisionSpecialFine() {
        mViewModel.saveRequestSupervisionSpecialFine(mJob, mQueryParam) {
            when (it) {
                Constant.ResultWrapper.Loading -> {
                    mLoadingDialog.show()
                }

                is Constant.ResultWrapper.Success -> {
                    mLoadingDialog.dismiss()

                    try {
                        val res = it.body.toMapper<SupervisionSpecialFineMapper>()
                        if (res.success == true) {
                            "عملیات موفق".toastMessage(Constant.ToastType.Success)
                            popBackStack()
                        } else "خطا در ثبت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "خطا در ثبت اطلاعات!".toastMessage(Constant.ToastType.Warning)
                    }
                }

                is Constant.ResultWrapper.Error -> {
                    mLoadingDialog.dismiss()
                    "خطا در ارسال اطلاعات!".toastMessage(Constant.ToastType.Error)
                }
            }
        }
    }

    override fun setOnBackPressed(it: FragmentActivity) {
        popBackStack()
    }

    override fun getViewBindingInflater(): FragmentFineBinding {
        return FragmentFineBinding.inflate(layoutInflater)
    }
}
