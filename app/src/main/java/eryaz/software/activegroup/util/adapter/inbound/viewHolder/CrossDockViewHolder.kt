package eryaz.software.activegroup.util.adapter.inbound.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.CrossDockDto
import eryaz.software.activegroup.databinding.ItemCrossDockListBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class CrossDockViewHolder(val binding: ItemCrossDockListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: CrossDockDto,
        onItemClick: ((CrossDockDto) -> Unit)
    ) {
        binding.dto = dto

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    companion object {
        fun from(parent: ViewGroup): CrossDockViewHolder {
            val binding = ItemCrossDockListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return CrossDockViewHolder(binding)
        }
    }
}