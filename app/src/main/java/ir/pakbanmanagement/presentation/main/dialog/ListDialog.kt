package ir.pakbanmanagement.presentation.main.dialog

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.BottomSheetMenuBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.presentation.main.adapter.BottomSheetMenuAdapter

class ListDialog(
    internal val context: Context,
    private val list: MutableList<BottomSheetMenuMapper>,
    private val isCancelable: Boolean = true,
    private val block: (Int) -> Unit = {},
) : BaseBottomSheetDialog<BottomSheetMenuBinding>(context) {

    private lateinit var mBottomSheetMenuAdapter: BottomSheetMenuAdapter

    override fun setOnView() {
        myView.apply {
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(isCancelable)

            imgCancel.setOnClickListener {
                dismiss()
            }

            mBottomSheetMenuAdapter = BottomSheetMenuAdapter {
                block.invoke(it)
                dismiss()
            }

            mBottomSheetMenuAdapter.setList(list)
            recyclerView.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = mBottomSheetMenuAdapter
            }
        }
    }

    override fun getViewBindingInflater(): BottomSheetMenuBinding {
        return BottomSheetMenuBinding.inflate(layoutInflater)
    }
}
