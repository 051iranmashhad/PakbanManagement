package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.ItemBottomSheetMenuBinding
import ir.pakbanmanagement.databinding.ItemSimpleBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.other.getColor2
import ir.pakbanmanagement.other.ifNullOrEmpty

class BottomSheetMenuWithSearchAdapter(
    private val onClick: (item: Int) -> Unit,
) : RecyclerView.Adapter<BottomSheetMenuWithSearchAdapter.MyViewHolder>() {

    private var mList: MutableList<BottomSheetMenuMapper> = mutableListOf()

    internal fun setList(list: MutableList<BottomSheetMenuMapper>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemSimpleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(position, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class MyViewHolder(private val mBinding: ItemSimpleBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindView(position: Int, item: BottomSheetMenuMapper) {
            mBinding.apply {
                root.setOnClickListener {
                    onClick.invoke(position)
                }

                txtTitle.text = item.title.ifNullOrEmpty()
            }
        }
    }
}
