package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.R
import ir.pakbanmanagement.databinding.ItemBottomSheetMenuBinding
import ir.pakbanmanagement.mapper.BottomSheetMenuMapper
import ir.pakbanmanagement.other.getColor2
import ir.pakbanmanagement.other.ifNullOrEmpty

class BottomSheetMenuAdapter(
    private val onClick: (item: Int) -> Unit,
) : RecyclerView.Adapter<BottomSheetMenuAdapter.MyViewHolder>() {

    private var mList: MutableList<BottomSheetMenuMapper> = mutableListOf()

    internal fun setList(list: MutableList<BottomSheetMenuMapper>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemBottomSheetMenuBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(position, mList[position])
    }

    override fun getItemCount() = mList.size

    inner class MyViewHolder(private val mBinding: ItemBottomSheetMenuBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindView(position: Int, item: BottomSheetMenuMapper) {
            mBinding.apply {
                root.setOnClickListener {
                    onClick.invoke(position)
                    item.checked = !item.checked
                }

                txtTitle.text = item.title.ifNullOrEmpty()

                txtTitle.setTextColor(
                    if (item.checked) {
                        itemView.context.getColor2(R.color.color_primary)
                    } else {
                        itemView.context.getColor2(R.color.color_black)
                    }
                )

                viewLine.visibility = if (position == mList.size - 1) View.INVISIBLE
                else View.VISIBLE
            }
        }
    }
}
