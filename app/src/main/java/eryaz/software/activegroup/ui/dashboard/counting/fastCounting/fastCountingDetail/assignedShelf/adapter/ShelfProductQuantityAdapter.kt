package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.StockTakingDetailDto

class ShelfProductQuantityAdapterForCounting :
    ListAdapter<StockTakingDetailDto, RecyclerView.ViewHolder>(DiffCallBacksShelf) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ShelfProductQuantityVH.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ShelfProductQuantityVH -> {
                holder.bind(
                    dto = getItem(position),
                    isLastItem = position == itemCount.minus(1)
                )
            }
        }
    }
}

object DiffCallBacksShelf : DiffUtil.ItemCallback<StockTakingDetailDto>() {
    override fun areItemsTheSame(
        oldItem: StockTakingDetailDto,
        newItem: StockTakingDetailDto
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: StockTakingDetailDto,
        newItem: StockTakingDetailDto
    ) = oldItem == newItem
}