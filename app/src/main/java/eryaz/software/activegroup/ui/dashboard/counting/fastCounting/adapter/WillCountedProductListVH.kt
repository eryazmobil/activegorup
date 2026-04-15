package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.CountingComparisonDto
import eryaz.software.activegroup.databinding.ItemCountingProductListBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener
import eryaz.software.activegroup.util.extensions.toIntOrZero

class WillCountedProductListVH(val binding: ItemCountingProductListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: CountingComparisonDto,
        onItemClick: ((CountingComparisonDto) -> Unit)
    ) {
        binding.dto = dto

        val oldQuantity = dto.oldQuantity.toIntOrZero()
        val newQuantity = dto.newQuantity.get().toIntOrZero()

        val backgroundColorRes = when {
            newQuantity == 0 -> R.color.logoGray
            newQuantity == oldQuantity -> R.color.colorSuccessGreen
            else -> R.color.colorDangerRed
        }

        binding.container.setBackgroundColor(
            ContextCompat.getColor(binding.root.context, backgroundColorRes)
        )

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