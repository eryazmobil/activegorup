package eryaz.software.activegroup.ui.dashboard.counting.fastCounting.fastCountingDetail.assignedShelf.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.ProductShelfQuantityDto
import eryaz.software.activegroup.data.models.dto.StockTakingDetailDto
import eryaz.software.activegroup.databinding.ItemStorageQuantityTextBinding

class ShelfProductQuantityVH(val binding: ItemStorageQuantityTextBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: StockTakingDetailDto,
        isLastItem: Boolean
    ) {
        binding.keyTxt.text = dto.shelfDto.shelfAddress

        binding.valueTxt.visibility = View.GONE
        binding.underline.isVisible = !isLastItem

        if (dto.isFinished) {
            binding.keyTxt.setBackgroundResource(R.color.finish_color)
        } else if (dto.isStarted) {
            binding.keyTxt.setBackgroundResource(R.color.blue_a1)
        }
    }

    companion object {
        fun from(parent: ViewGroup): ShelfProductQuantityVH {
            val binding = ItemStorageQuantityTextBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ShelfProductQuantityVH(binding)
        }
    }
}