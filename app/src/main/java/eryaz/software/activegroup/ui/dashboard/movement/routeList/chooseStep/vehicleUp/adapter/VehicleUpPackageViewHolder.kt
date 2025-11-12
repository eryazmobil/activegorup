package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.VehiclePackageDto
import eryaz.software.activegroup.databinding.ItemUpVehiclePackageBinding
import eryaz.software.activegroup.util.bindingAdapter.setOnSingleClickListener

class VehicleUpPackageViewHolder(val binding: ItemUpVehiclePackageBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dto: VehiclePackageDto,
        onItemClick: ((VehiclePackageDto) -> Unit)
    ) {
        binding.dto = dto

        binding.root.setOnSingleClickListener {
            onItemClick(dto)
        }
    }

    companion object {
        fun from(parent: ViewGroup): VehicleUpPackageViewHolder {
            val binding = ItemUpVehiclePackageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return VehicleUpPackageViewHolder(binding)
        }
    }
}