package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.CountingComparisonDto
import eryaz.software.activegroup.databinding.ItemCountingProductListBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class WillCountedProductListVH(val binding: ItemCountingProductListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: CountingComparisonDto,
        onItemClick: ((CountingComparisonDto) -> Unit)
    ) {
        binding.dto = dto

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    companion object {
        fun from(parent: ViewGroup): WillCountedProductListVH {
            val binding = ItemCountingProductListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return WillCountedProductListVH(binding)
        }
    }
}