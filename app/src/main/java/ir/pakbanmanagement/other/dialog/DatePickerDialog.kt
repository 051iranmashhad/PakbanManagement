package ir.pakbanmanagement.other.dialog

import android.content.Context
import ir.pakbanmanagement.databinding.BottomSheetDatePickerBinding
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.other.getLocalDate

class DatePickerDialog(
    internal val context: Context,
    internal val initialDate: String = getLocalDate(),
    internal val block: (String) -> Unit,
) : BaseBottomSheetDialog<BottomSheetDatePickerBinding>(context) {

    override fun setOnView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        myView.apply {

            imgCancel.setOnClickListener {
                cancel()
            }

            if (initialDate.isNotEmpty()) {
                datePicker.setInitialSelectedDate(initialDate)
            }

            btnSubmit.setOnClickListener {
                block.invoke(datePicker.getCurrentlySelectedDate3())
                dismiss()
                cancel()
            }
        }
    }

    override fun getViewBindingInflater(): BottomSheetDatePickerBinding {
        return BottomSheetDatePickerBinding.inflate(layoutInflater)
    }
}