package eryaz.software.activegroup.ui.dashboard.movement.routeList.chooseStep.vehicleUp.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.VehiclePackageDto

class VehicleUpPackageAdapter :
    ListAdapter<VehiclePackageDto, RecyclerView.ViewHolder>(WorkActivityDiffCallBack) {

    var onItemClick : ((VehiclePackageDto) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VehicleUpPackageViewHolder.Companion.from(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VehicleUpPackageViewHolder -> holder.bind(getItem(position),onItemClick)
        }
    }
}

object WorkActivityDiffCallBack : DiffUtil.ItemCallback<VehiclePackageDto>() {
    override fun areItemsTheSame(
        oldItem: VehiclePackageDto,
        newItem: VehiclePackageDto
    ) = oldItem.code == newItem.code

    override fun areContentsTheSame(
        oldItem: VehiclePackageDto,
        newItem: VehiclePackageDto
    ) = oldItem == newItem
}
