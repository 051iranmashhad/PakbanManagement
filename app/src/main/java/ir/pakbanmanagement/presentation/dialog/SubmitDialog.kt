package ir.pakbanmanagement.presentation.dialog

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import ir.pakbanmanagement.databinding.BottomSheetSubmitBinding
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.other.Constant

class SubmitDialog(
    internal val context: FragmentActivity,
    private val title: String = Constant.Key.EMPTY,
    private val subTitle: String = Constant.Key.EMPTY,
    private val positiveText: String = "تایید",
    private val negativeText: String = "انصراف",
    private val isNegative: Boolean = true,
    private val isCancelable: Boolean = true,
    private val dismiss: () -> Unit = {},
    private val block: () -> Unit,
) : BaseBottomSheetDialog<BottomSheetSubmitBinding>(context) {

    override fun setOnView() {
        setCancelable(isCancelable)
        setCanceledOnTouchOutside(isCancelable)

        myView.apply {
            imgCancel.visibility = if (isCancelable) View.VISIBLE else View.INVISIBLE
            imgCancel.setOnClickListener {
                cancel()
            }

            txtTitle.text = title
            txtSubTitle.text = subTitle

            btnCancel.isVisible = isNegative
            btnCancel.text = negativeText
            btnCancel.setOnClickListener {
                dismiss.invoke()
                dismiss()
                cancel()
            }

            btnSubmit.text = positiveText
            btnSubmit.setOnClickListener {
                block.invoke()
                dismiss()
                cancel()
            }
        }
    }

    override fun getViewBindingInflater(): BottomSheetSubmitBinding {
        return BottomSheetSubmitBinding.inflate(layoutInflater)
    }
}
