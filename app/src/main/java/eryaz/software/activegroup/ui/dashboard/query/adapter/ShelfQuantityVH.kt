package eryaz.software.activegroup.ui.dashboard.query.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.ProductShelfQuantityDto
import eryaz.software.activegroup.databinding.ItemStorageQuantityTextBinding

class ShelfQuantityVH(val binding: ItemStorageQuantityTextBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: ProductShelfQuantityDto,
        isLastItem: Boolean
    ) {
        binding.keyTxt.text = dto.shelf?.shelfAddress
        binding.valueTxt.text = dto.quantity

        binding.underline.isVisible = !isLastItem
    }

    companion object {
        fun from(parent: ViewGroup): ShelfQuantityVH {
            val binding = ItemStorageQuantityTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ShelfQuantityVH(binding)
        }
    }
}