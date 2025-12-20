package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.ItemSimpleBinding
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.other.ifNullOrEmpty

class PriceListSeasonChildAdapter(
    private val onClick: (PriceListSeasonMapper.Data.PriceRowDTOs) -> Unit,
) : RecyclerView.Adapter<PriceListSeasonChildAdapter.MyViewHolder>() {

    private var mList: MutableList<PriceListSeasonMapper.Data.PriceRowDTOs> = mutableListOf()

    internal fun setList(list: MutableList<PriceListSeasonMapper.Data.PriceRowDTOs>) {
        mList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemSimpleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class MyViewHolder(private val mBinding: ItemSimpleBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindView(item: PriceListSeasonMapper.Data.PriceRowDTOs) {
            mBinding.apply {
                root.setOnClickListener {
                    onClick.invoke(item)
                }

                txtTitle.text = buildString {
                    append(item.title.ifNullOrEmpty())
                    append(" | ")
                    append(item.code.ifNullOrEmpty())
                }
                imgIcon.visibility = View.INVISIBLE
            }
        }
    }
}
