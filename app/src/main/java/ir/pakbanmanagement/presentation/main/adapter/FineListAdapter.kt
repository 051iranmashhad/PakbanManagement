package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.ItemSimpleBinding
import ir.pakbanmanagement.mapper.HistoryFineListMapper
import ir.pakbanmanagement.other.ifNullOrEmpty
import ir.pakbanmanagement.other.invisible

class FineListAdapter : RecyclerView.Adapter<FineListAdapter.MyViewHolder>() {

    private var mList: MutableList<HistoryFineListMapper.Data.SupSepcialFineDetail> =
        mutableListOf()

    internal fun setList(list: MutableList<HistoryFineListMapper.Data.SupSepcialFineDetail>) {
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

        fun bindView(item: HistoryFineListMapper.Data.SupSepcialFineDetail) {
            mBinding.apply {
                txtTitle.text = item.completePath.ifNullOrEmpty()
                imgIcon.invisible()
            }
        }
    }
}
