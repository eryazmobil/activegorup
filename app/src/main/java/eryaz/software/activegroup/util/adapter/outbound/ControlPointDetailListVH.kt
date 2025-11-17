package eryaz.software.activegroup.util.adapter.outbound

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eryaz.software.activegroup.data.models.dto.OrderDetailDto
import eryaz.software.activegroup.databinding.ItemControlPointDetailBinding

class ControlPointDetailListVH(val binding: ItemControlPointDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private var currentDto: OrderDetailDto? = null

    fun bind(dto: OrderDetailDto) {
        currentDto = dto
        binding.dto = dto
        binding.executePendingBindings()

        setStatus(dto)
    }

    fun animateBackground() {
        binding.itemParent.setBackgroundColor(Color.YELLOW)
        binding.apply {
            productName.setTextColor(Color.BLACK)
            orderQuantityTxt.setTextColor(Color.BLACK)
            collectedQuantityTxt.setTextColor(Color.BLACK)
            controlledTxt.setTextColor(Color.BLACK)
        }

        binding.itemParent.animate()
            .alpha(0.3f)
            .setDuration(500)
            .withEndAction {
                binding.itemParent.animate()
                    .alpha(1f)
                    .setDuration(500)
                    .withEndAction {
                        currentDto?.let { dto ->
                            setStatus(dto)
                        }
                    }
                    .start()
            }
            .start()
    }

    private fun setStatus(dto: OrderDetailDto) {
        if (dto.quantityCollected.toInt() == dto.quantityShipped.toInt() && dto.quantityShipped.toInt() != 0) {
            binding.itemParent.setBackgroundColor(Color.parseColor("#009900"))
            binding.apply {
                productName.setTextColor(Color.WHITE)
                orderQuantityTxt.setTextColor(Color.WHITE)
                collectedQuantityTxt.setTextColor(Color.WHITE)
                controlledTxt.setTextColor(Color.WHITE)
            }
        } else if (dto.quantity.toInt() != dto.quantityCollected.toInt()) {
            binding.itemParent.setBackgroundColor(Color.parseColor("#EA2600"))
            binding.apply {
                productName.setTextColor(Color.WHITE)
                orderQuantityTxt.setTextColor(Color.WHITE)
                collectedQuantityTxt.setTextColor(Color.WHITE)
                controlledTxt.setTextColor(Color.WHITE)
            }
        } else {
            binding.itemParent.setBackgroundColor(Color.TRANSPARENT)
            binding.apply {
                productName.setTextColor(Color.BLACK)
                orderQuantityTxt.setTextColor(Color.BLACK)
                collectedQuantityTxt.setTextColor(Color.BLACK)
                controlledTxt.setTextColor(Color.BLACK)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ControlPointDetailListVH {
            val binding = ItemControlPointDetailBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ControlPointDetailListVH(binding)
        }
    }
}