package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.ItemImageBinding
import ir.pakbanmanagement.mapper.ImageListMapper
import ir.pakbanmanagement.other.setImageBase64
import ir.pakbanmanagement.other.setRoundedCorners
import ir.pakbanmanagement.other.toPx
import kotlin.collections.indexOfFirst
import kotlin.collections.toMutableList

class ImageListAdapter(
    private val onClick: (ImageListMapper) -> Unit,
) : RecyclerView.Adapter<ImageListAdapter.MyViewHolder>() {

    private var mList: MutableList<ImageListMapper> = mutableListOf()

    internal fun setList(list: MutableList<ImageListMapper>) {
        mList = list
        notifyDataSetChanged()
    }

    internal fun removeItem(item: ImageListMapper) {
        val position = mList.indexOfFirst { it.base64Data == item.base64Data }
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
    }

    internal fun getList() = mList.toMutableList()

    internal fun addItem(item: ImageListMapper) {
        mList.add(0, item)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class MyViewHolder(private val mBinding: ItemImageBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindView(item: ImageListMapper) {
            mBinding.apply {
                itemView.setOnClickListener {
                    onClick.invoke(item)
                }
                img.setRoundedCorners(8f.toPx())
                img.setImageBase64(item.base64Data)
            }
        }
    }
}
