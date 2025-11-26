package eryaz.software.activegroup.util.adapter.movement.packageList

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.R
import eryaz.software.activegroup.data.models.dto.VehiclePackageDto
import eryaz.software.activegroup.databinding.ItemOnVehiclePackageBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class VehiclePackageViewHolder(val binding: ItemOnVehiclePackageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: VehiclePackageDto,
        onItemClick: ((VehiclePackageDto) -> Unit)
    ) {
        binding.dto = dto

        val strokeColor = if (dto.warehouse.code.isEmpty()) {
            R.color.colorDangerRed
        } else {
            R.color.colorSuccessGreen
        }

        binding.cardView.strokeColor = ContextCompat.getColor(binding.root.context, strokeColor)
        binding.cardView.strokeWidth = 4.dpToPx(binding.root.context)

        binding.executePendingBindings()

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    companion object {
        fun from(parent: ViewGroup): VehiclePackageViewHolder {
            val binding = ItemOnVehiclePackageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return VehiclePackageViewHolder(binding)
        }
    }
}