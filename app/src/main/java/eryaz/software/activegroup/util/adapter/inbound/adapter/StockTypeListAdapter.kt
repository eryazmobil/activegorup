package eryaz.software.activegroup.util.adapter.inbound.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.StockTypeDto
import eryaz.software.activegroup.util.adapter.inbound.viewHolder.StockTypeListViewHolder

class StockTypeListAdapter :
    ListAdapter<StockTypeDto, RecyclerView.ViewHolder>(StockTypeDiffCallBack) {

    var onItemClick: ((StockTypeDto) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StockTypeListViewHolder.from(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StockTypeListViewHolder -> holder.bind(getItem(position), onItemClick)
        }
    }
}

object StockTypeDiffCallBack : DiffUtil.ItemCallback<StockTypeDto>() {
    override fun areItemsTheSame(
        oldItem: StockTypeDto,
        newItem: StockTypeDto
    ) = oldItem.type == newItem.type

    override fun areContentsTheSame(
        oldItem: StockTypeDto,
        newItem: StockTypeDto
    ) = oldItem == newItem
}
