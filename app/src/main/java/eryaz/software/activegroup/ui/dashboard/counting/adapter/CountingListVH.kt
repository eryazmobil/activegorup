package eryaz.software.activegroup.ui.dashboard.counting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.StockTakingHeaderDto
import eryaz.software.activegroup.databinding.ItemCountingWorkActivityListBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class CountingListVH(val binding: ItemCountingWorkActivityListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: StockTakingHeaderDto,
        onItemClick: ((StockTakingHeaderDto) -> Unit)
    ) {
        binding.dto = dto

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    companion object {
        fun from(parent: ViewGroup): eryaz.software.activegroup.ui.dashboard.counting.adapter.CountingListVH {
            val binding = ItemCountingWorkActivityListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return CountingListVH(binding)
        }
    }
}