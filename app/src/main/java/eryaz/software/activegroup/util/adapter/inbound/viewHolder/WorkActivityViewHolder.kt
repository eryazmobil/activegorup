package eryaz.software.activegroup.util.adapter.inbound.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.WorkActivityDto
import eryaz.software.activegroup.databinding.ItemWorkActivityListBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class WorkActivityViewHolder(val binding: ItemWorkActivityListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: WorkActivityDto,
        onItemClick: ((WorkActivityDto) -> Unit)
    ) {
        binding.dto = dto

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    companion object {
        fun from(parent: ViewGroup): WorkActivityViewHolder {
            val binding = ItemWorkActivityListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return WorkActivityViewHolder(binding)
        }
    }
}