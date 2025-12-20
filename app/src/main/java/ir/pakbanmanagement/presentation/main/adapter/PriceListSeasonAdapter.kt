package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.ItemSimpleBinding
import ir.pakbanmanagement.mapper.PriceListSeasonMapper
import ir.pakbanmanagement.other.ifNullOrEmpty

class PriceListSeasonAdapter(
    private val onClick: (PriceListSeasonMapper.Data) -> Unit,
) : RecyclerView.Adapter<PriceListSeasonAdapter.MyViewHolder>() {

    private var mList: MutableList<PriceListSeasonMapper.Data> = mutableListOf()

    internal fun setList(list: MutableList<PriceListSeasonMapper.Data>) {
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

        fun bindView(item: PriceListSeasonMapper.Data) {
            mBinding.apply {
                root.setOnClickListener {
                    onClick.invoke(item)
                }

                txtTitle.text = buildString {
                    append(item.title.ifNullOrEmpty())
                    append(" | ")
                    append(item.code.ifNullOrEmpty())
                }
                imgIcon.visibility =
                    if (item.priceListRowDTOs.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

            }
        }
    }
}
