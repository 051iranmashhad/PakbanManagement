package ir.pakbanmanagement.presentation.main.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.BottomSheetMenuBinding
import ir.pakbanmanagement.databinding.BottomSheetMenuWithSearchBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.toEnglishNumber
import ir.pakbanmanagement.presentation.main.adapter.BottomSheetMenuWithSearchAdapter
import ir.pakbanmanagement.presentation.main.adapter.PriceListSeasonAdapter

class ListWithSearchDialog(
    internal val context: Context,
    private val list: MutableList<BottomSheetMenuMapper>,
    private val isCancelable: Boolean = true,
    private val block: (Int) -> Unit = {},
) : BaseBottomSheetDialog<BottomSheetMenuWithSearchBinding>(context) {

    private lateinit var mBottomSheetMenuWithSearchAdapter: BottomSheetMenuWithSearchAdapter
    private lateinit var mFilteredList: MutableList<BottomSheetMenuMapper>

    private val mLastList: MutableList<BottomSheetMenuMapper> get() = list

    override fun setOnView() {
        myView.apply {
            setCancelable(isCancelable)
            setCanceledOnTouchOutside(isCancelable)

            val dm = context.resources.displayMetrics.heightPixels
            root.layoutParams.height = (dm * 2) / 3

            imgCancel.setOnClickListener {
                dismiss()
            }

            mBottomSheetMenuWithSearchAdapter = BottomSheetMenuWithSearchAdapter {
                block.invoke(it)
                dismiss()
            }

            recyclerView.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = mBottomSheetMenuWithSearchAdapter
            }
            mBottomSheetMenuWithSearchAdapter.setList(list)

            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    mJob.coroutineMain {
                        try {
                            val txt = edtSearch.text.toString().toEnglishNumber().lowercase()
                            mFilteredList = mLastList.filter {
                                it.title.lowercase()
                                    .contains(txt) || it.title.lowercase()
                                    .contains(txt)
                            }.toMutableList()
                            mBottomSheetMenuWithSearchAdapter.setList(mFilteredList)
                            if (mBottomSheetMenuWithSearchAdapter.itemCount == 0) {
                                recyclerView.isVisible = false
                                txtEmpty.isVisible = true
                                txtEmpty.text = "داده ای پیدا نشد!"
                            } else {
                                recyclerView.isVisible = true
                                txtEmpty.isVisible = false
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int,
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int,
                ) {

                }
            })
        }
    }

    override fun getViewBindingInflater(): BottomSheetMenuWithSearchBinding {
        return BottomSheetMenuWithSearchBinding.inflate(layoutInflater)
    }
}
