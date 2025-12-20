package ir.pakbanmanagement.presentation.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ir.pakbanmanagement.databinding.ItemMenuBinding
import ir.pakbanmanagement.mapper.MenuMapper
import ir.pakbanmanagement.other.Constant

class MenuAdapter(
    private val onClick: (key: String) -> Unit,
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private var mList: List<MenuMapper> = Constant.menuList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyViewHolder(
            ItemMenuBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(mList[position])
    }

    override fun getItemCount() = mList.size

    inner class MyViewHolder(private val mBinding: ItemMenuBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bindView(item: MenuMapper) {
            mBinding.apply {
                layoutMenu.setOnClickListener {
                    onClick.invoke(item.id)
                }

                txtName.text = item.title
                imgIcon.setImageResource(item.icon)
            }
        }
    }
}
