package ir.pakbanmanagement.other.dialog

import android.content.Context
import ir.pakbanmanagement.databinding.BottomSheetTimePickerBinding
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.other.getLocalTime

class TimePickerDialog(
    internal val context: Context,
    internal val initialTime: String = getLocalTime(),
    internal val block: (String) -> Unit,
) : BaseBottomSheetDialog<BottomSheetTimePickerBinding>(context) {

    override fun setOnView() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        myView.apply {

            imgCancel.setOnClickListener {
                cancel()
            }

            timePicker.setInitialSelectedTime("$initialTime Am")

            btnSubmit.setOnClickListener {
                block.invoke(
                    timePicker.getCurrentlySelectedTime().replace(" ", "").replace("24", "00")
                        .trim()
                )
                cancel()
            }
        }
    }

    override fun getViewBindingInflater(): BottomSheetTimePickerBinding {
        return BottomSheetTimePickerBinding.inflate(layoutInflater)
    }
}
