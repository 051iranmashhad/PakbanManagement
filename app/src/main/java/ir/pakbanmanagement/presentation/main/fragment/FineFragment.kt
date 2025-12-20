package ir.pakbanmanagement.presentation.main.fragment

import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.FragmentFineBinding
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.other.BaseFragmentWithViewModel
import ir.pakbanmanagement.other.Constant
import ir.pakbanmanagement.other.base.BaseViewModel
import ir.pakbanmanagement.other.ifNullOrEmpty
import ir.pakbanmanagement.other.popBackStack
import ir.pakbanmanagement.other.toastMessage

@AndroidEntryPoint
class FineFragment(
    private val priceListSeasonDataMapper: PriceListSeasonMapper.Data? = null,
    private val priceListSeasonDataPriceRowDTOsMapper: PriceListSeasonMapper.Data.PriceRowDTOs? = null,
) : BaseFragmentWithViewModel<BaseViewModel, FragmentFineBinding>(BaseViewModel::class.java) {

    private var mCheckBox = false

    override fun setOnView() {
        myView.apply {

            imgBack.setOnClickListener {
                popBackStack()
            }

            txtTitle.text = run {
                if (priceListSeasonDataMapper != null) priceListSeasonDataMapper.title
                else priceListSeasonDataPriceRowDTOsMapper?.title
            }.ifNullOrEmpty()

            layoutCheckBox.setOnClickListener {
                imgCheckBox.setImageResource(if (mCheckBox) R.drawable.ic_un_check_box else R.drawable.ic_check_box)
                mCheckBox = !mCheckBox
            }

            imgFine.setOnClickListener {
                "در حال پیاده سازی!".toastMessage(Constant.ToastType.Info)
            }

            btnSubmit.setOnClickListener {
                "در حال پیاده سازی!".toastMessage(Constant.ToastType.Info)
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
