package ir.pakbanmanagement.presentation.main.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.BottomSheetFineListBinding
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.other.BaseBottomSheetDialog
import ir.pakbanmanagement.other.coroutineMain
import ir.pakbanmanagement.other.toEnglishNumber
import ir.pakbanmanagement.presentation.main.adapter.PriceListSeasonChildAdapter
import kotlinx.coroutines.CompletableJob

class FineChildListDialog(
    internal val context: Context,
    private val supervisor: CompletableJob,
    private val list: MutableList<PriceListSeasonMapper.Data.PriceRowDTOs>,
    private val block: (PriceListSeasonMapper.Data.PriceRowDTOs) -> Unit,
) : BaseBottomSheetDialog<BottomSheetFineListBinding>(context) {

    private lateinit var mFilteredList: MutableList<PriceListSeasonMapper.Data.PriceRowDTOs>
    private lateinit var mPriceListSeasonChildAdapter: PriceListSeasonChildAdapter

    private val mLastList: MutableList<PriceListSeasonMapper.Data.PriceRowDTOs> get() = list

    override fun setOnView() {
        myView.apply {
            val dm = context.resources.displayMetrics.heightPixels
            root.layoutParams.height = dm - (dm / 2)

            imgCancel.setOnClickListener {
                dismiss()
            }
            mPriceListSeasonChildAdapter = PriceListSeasonChildAdapter { item ->
                block.invoke(item)
                dismiss()
            }

            recyclerView.apply {
                setHasFixedSize(true)
                setRecycledViewPool(RecyclerView.RecycledViewPool())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                adapter = mPriceListSeasonChildAdapter
            }
            mPriceListSeasonChildAdapter.setList(list)

            edtSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    supervisor.coroutineMain {
                        try {
                            val txt = edtSearch.text.toString().toEnglishNumber().lowercase()
                            mFilteredList = mLastList.filter {
                                it.title?.lowercase()
                                    ?.contains(txt) == true || it.title?.lowercase()
                                    ?.contains(txt) == true
                            }.toMutableList()
                            mPriceListSeasonChildAdapter.setList(mFilteredList)
                            if (mPriceListSeasonChildAdapter.itemCount == 0) {
                                recyclerView.isVisible = false
                                txtEmpty.isVisible = true
                                txtEmpty.text = "کد موجود نیست!"
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

    override fun getViewBindingInflater(): BottomSheetFineListBinding {
        return BottomSheetFineListBinding.inflate(layoutInflater)
    }
}
