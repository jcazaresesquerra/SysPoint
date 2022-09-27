package com.app.syspoint.ui.ventas.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.databinding.ItemListaVentasBinding
import com.app.syspoint.repository.database.bean.VentasBean
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click

class AdapterListaVentas(
    data: List<VentasBean?>,
    val onItemClickListener: OnItemClickListener
): RecyclerView.Adapter<AdapterListaVentas.Holder>() {

    private var mData = data

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemListaVentasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(mData[position], onItemClickListener)
    }

    override fun getItemCount(): Int = if (mData.isEmpty()) 0 else mData.size

    fun setData(data: List<VentasBean?>) {
        mData = data
        notifyDataSetChanged()
    }

    class Holder(val binding: ItemListaVentasBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(ventasBean: VentasBean?, onItemClickListener: OnItemClickListener) {
            ventasBean?.let { venta ->
                binding.textViewListaVentaClienteView.text = venta.cliente.cuenta
                binding.textViewListaVentaClienteNombreView.text = venta.cliente.nombre_comercial
                binding.textViewListaVentaFechaView.text = venta.fecha
                binding.textViewListaVentaEstadoView.text = venta.estado
                binding.textViewListaVentaImporteView.text = Utils.formatMoneyMX(venta.importe + venta.impuesto)

                binding.textViewListaVentaEstadoView.apply {
                    if (venta.estado.compareTo("CA", ignoreCase = true) == 0) {
                        setTextColor(Color.RED)
                        text = "CANCELADO"
                    } else {
                        setTextColor(Color.GREEN)
                        text = "CONFIRMADO"
                    }
                }

                itemView click  { onItemClickListener.onItemClick(adapterPosition) }
            }
        }
    }
}