package eryaz.software.activegroup.util.adapter.movement.route

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.remote.response.DriverResponse

class RouteAdapter :
    ListAdapter<DriverResponse, RecyclerView.ViewHolder>(WorkActivityDiffCallBackP) {

    var onItemClick : ((DriverResponse) -> Unit) = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RouteViewHolder.from(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is RouteViewHolder -> holder.bind(getItem(position), onItemClick)
        }
    }
}

object WorkActivityDiffCallBackP : DiffUtil.ItemCallback<DriverResponse>() {
    override fun areItemsTheSame(
        oldItem: DriverResponse,
        newItem: DriverResponse
    ) = oldItem.code == newItem.code

    override fun areContentsTheSame(
        oldItem: DriverResponse,
        newItem: DriverResponse
    ) = oldItem == newItem
}
