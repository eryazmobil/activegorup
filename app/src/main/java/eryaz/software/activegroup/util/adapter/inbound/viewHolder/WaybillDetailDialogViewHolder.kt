package eryaz.software.activegroup.util.adapter.inbound.viewHolder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.WaybillListDetailDto
import eryaz.software.activegroup.databinding.WaybillDetailListTemBinding

class WaybillDetailDialogViewHolder(val binding: WaybillDetailListTemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(dto: WaybillListDetailDto) {
        binding.dto = dto
        binding.executePendingBindings()
        setControl(dto)
        setStatus(dto)
    }

    private fun setControl(dto: WaybillListDetailDto) {
        val (statusName, statusColor) = when {
            dto.quantityControlled == dto.quantity -> "TAM" to "#4caf50"
            dto.quantityControlled < dto.quantity -> "EKSIK" to "#df0029"
            else -> "FAZLA" to "#ffb822"
        }

        binding.controlValueTxt.apply {
            text = statusName
            setBackgroundColor(statusColor.toColorInt())
        }
    }


    private fun setStatus(dto: WaybillListDetailDto) {
        val (statusName, statusColor) = when {
            dto.quantityControlled == 0 -> "ÜRÜN YOK" to "#ffb822"
            dto.quantityControlled > 0 &&
                    dto.getQuantityForPlacementRemaining(
                        dto.quantityControlled,
                        dto.quantity,
                        dto.quantityPlaced
                    ) <= 0 -> "BİTTİ" to "#4caf50"
            else -> "BİTMEDİ" to "#df0029"
        }

        binding.statusValueTxt.apply {
            text = statusName
            setBackgroundColor(statusColor.toColorInt())
        }
    }

    companion object {
        fun from(parent: ViewGroup): WaybillDetailDialogViewHolder {
            val binding = WaybillDetailListTemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return WaybillDetailDialogViewHolder(binding)
        }
    }
}